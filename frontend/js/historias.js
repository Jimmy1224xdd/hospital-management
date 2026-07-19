/**
 * historias.js - Gestion de historias clinicas en el frontend
 *
 * BUGS INTENCIONALES:
 * 1. Permite insertar HTML/scripts en el diagnostico (XSS almacenado)
 * 2. No sanitiza los datos que vienen del backend antes de mostrarlos
 * 3. No valida que el paciente exista antes de buscar sus historias
 */

const HistoriasModule = {
    historiasCache: [],
    pacientesCache: [],
    doctoresCache: [],

    async init() {
        await Promise.all([
            this.cargarHistorias(),
            this.cargarPacientes(),
            this.cargarDoctores(),
        ]);
        this.setupListeners();
    },

    async cargarHistorias() {
        try {
            const historias = await HistoriasAPI.listar();
            this.historiasCache = historias;
            this.renderTabla(historias);
        } catch (error) {
            console.log('Error al cargar historias clinicas');
        }
    },

    async cargarPacientes() {
        try {
            this.pacientesCache = await PacientesAPI.listar();
        } catch (e) { /* silencioso */ }
    },

    async cargarDoctores() {
        try {
            this.doctoresCache = await DoctoresAPI.listar();
        } catch (e) { /* silencioso */ }
    },

    renderTabla(historias) {
        const tbody = document.querySelector('#historias-table tbody');
        if (!historias || historias.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5">No hay historias clinicas registradas</td></tr>';
            return;
        }

        tbody.innerHTML = historias.map(h => {
            const pacienteNombre = h.paciente
                ? `${escapeHTML(h.paciente.nombre)} ${escapeHTML(h.paciente.apellido)}`
                : 'N/A';
            const doctorNombre = h.doctor
                ? `${escapeHTML(h.doctor.nombre)} ${escapeHTML(h.doctor.apellido)}`
                : 'N/A';

            return `
                <tr>
                    <td>${pacienteNombre}</td>
                    <td>${doctorNombre}</td>
                    <td>${formatDateTime(h.fechaCreacion)}</td>
                    <td>${escapeHTML(h.diagnostico)}</td>
                    <td class="actions">
                        <button class="btn-view" onclick="HistoriasModule.verHistoria(${h.id})">Ver</button>
                    </td>
                </tr>
            `;
        }).join('');
    },

    setupListeners() {
        document.getElementById('btn-nueva-historia').onclick = () => this.mostrarFormulario();
        document.getElementById('historia-form').onsubmit = (e) => this.guardarHistoria(e);
    },

    mostrarFormulario() {
        const modal = document.getElementById('modal-historia');
        const form = document.getElementById('historia-form');
        form.reset();

        // Llenar select de pacientes
        const selectPaciente = document.getElementById('historia-paciente');
        selectPaciente.innerHTML = '<option value="">Seleccione un paciente</option>' +
            this.pacientesCache.map(p =>
                `<option value="${p.id}">${p.nombre} ${p.apellido}</option>`
            ).join('');

        // Llenar select de doctores
        const selectDoctor = document.getElementById('historia-doctor');
        selectDoctor.innerHTML = '<option value="">Seleccione un doctor (opcional)</option>' +
            this.doctoresCache.map(d =>
                `<option value="${d.id}">${d.nombre} ${d.apellido}</option>`
            ).join('');

        modal.classList.add('show');
    },

    cerrarFormulario() {
        document.getElementById('modal-historia').classList.remove('show');
    },

    async guardarHistoria(e) {
        e.preventDefault();

        const historiaData = {
            pacienteId: parseInt(document.getElementById('historia-paciente').value),
            doctorId: document.getElementById('historia-doctor').value
                ? parseInt(document.getElementById('historia-doctor').value)
                : null,
            diagnostico: document.getElementById('historia-diagnostico').value,
            tratamiento: document.getElementById('historia-tratamiento').value,
            observaciones: document.getElementById('historia-observaciones').value,
        };

        if (!historiaData.pacienteId || isNaN(historiaData.pacienteId)) {
            return showAlert('El paciente es obligatorio', 'error');
        }
        if (!historiaData.diagnostico || historiaData.diagnostico.trim() === '') {
            return showAlert('El diagnóstico es obligatorio', 'error');
        }

        try {
            await HistoriasAPI.crear(historiaData);
            showAlert('Historia clinica creada exitosamente', 'success');
            this.cerrarFormulario();
            await this.cargarHistorias();
        } catch (error) {
            showAlert('Error al guardar la historia clinica', 'error');
        }
    },

    async verHistoria(id) {
        try {
            const h = await HistoriasAPI.buscar(id);
            const detalle = `
                <div style="text-align:left">
                    <p><strong>Paciente:</strong> ${escapeHTML(h.paciente?.nombre) || 'N/A'} ${escapeHTML(h.paciente?.apellido) || ''}</p>
                    <p><strong>Doctor:</strong> ${escapeHTML(h.doctor?.nombre) || 'N/A'} ${escapeHTML(h.doctor?.apellido) || ''}</p>
                    <p><strong>Fecha:</strong> ${formatDateTime(h.fechaCreacion)}</p>
                    <p><strong>Diagnostico:</strong> ${escapeHTML(h.diagnostico)}</p>
                    <p><strong>Tratamiento:</strong> ${escapeHTML(h.tratamiento) || 'No especificado'}</p>
                    <p><strong>Observaciones:</strong> ${escapeHTML(h.observaciones) || 'Ninguna'}</p>
                </div>
            `;

            // BUG: Crea un div con innerHTML para mostrar el detalle (XSS)
            const modal = document.getElementById('modal-historia');
            const detailDiv = document.createElement('div');
            detailDiv.innerHTML = detalle;
            detailDiv.style.padding = '1rem';

            // Remueve el form y muestra detalle
            const form = document.getElementById('historia-form');
            form.style.display = 'none';
            const existing = modal.querySelector('.historia-detalle');
            if (existing) existing.remove();
            detailDiv.className = 'historia-detalle';
            modal.appendChild(detailDiv);

            modal.classList.add('show');
        } catch (error) {
            showAlert('Error al cargar historia clinica', 'error');
        }
    },
};
