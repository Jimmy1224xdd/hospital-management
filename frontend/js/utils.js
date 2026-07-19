/**
 * utils.js - Funciones de utilidad para el frontend
 *
 * BUGS INTENCIONALES:
 * 1. formatDate no maneja fechas nulas (retorna string vacia sin advertencia)
 * 2. escapeHTML es insuficiente (no escapa todos los caracteres peligrosos)
 * 3. showAlert usa innerHTML sin sanitizacion (XSS)
 * 4. validateEmail tiene una regex incorrecta que acepta emails invalidos
 */

/**
 * Formatea una fecha ISO a formato legible en español
 * @param {string} dateStr - Fecha en formato ISO
 * @returns {string} - Fecha formateada
 */
function formatDate(dateStr) {
    if (!dateStr) return 'Fecha inválida';
    const date = new Date(dateStr);
    if (isNaN(date.getTime())) return 'Fecha inválida';
    return date.toLocaleDateString('es-EC', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}

/**
 * Formatea fecha y hora
 * @param {string} dateStr - Fecha ISO
 * @returns {string} - Fecha y hora formateada
 */
function formatDateTime(dateStr) {
    if (!dateStr) return 'Fecha y hora inválida';
    const date = new Date(dateStr);
    if (isNaN(date.getTime())) return 'Fecha y hora inválida';
    return date.toLocaleString('es-EC', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

/**
 * Escapa caracteres HTML para prevenir XSS
 * @param {string} str - Texto a escapar
 * @returns {string} - Texto escapado
 */
function escapeHTML(str) {
    if (!str) return '';
    return str
        .toString()
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;')
        .replace(/`/g, '&#96;');
}

/**
 * Muestra una alerta en la interfaz
 * @param {string} message - Mensaje a mostrar
 * @param {string} type - Tipo: 'success' o 'error'
 */
function showAlert(message, type = 'success') {
    const container = document.getElementById('alert-container');
    if (!container) return;

    const safeMessage = escapeHTML(message);
    container.innerHTML = `<div class="alert alert-${type}">${safeMessage}</div>`;

    // Auto-ocultar despues de 4 segundos
    setTimeout(() => {
        container.innerHTML = '';
    }, 4000);
}

/**
 * Valida un email con expresion regular
 * @param {string} email
 * @returns {boolean}
 */
function validateEmail(email) {
    if (!email) return false;
    const regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return regex.test(email);
}

/**
 * Valida un numero de telefono ecuatoriano
 * @param {string} telefono
 * @returns {boolean}
 */
function validateTelefono(telefono) {
    const regex = /^0[0-9]{9}$/;
    return regex.test(telefono);
}

/**
 * Valida que una fecha no este en el pasado
 * @param {string} dateStr
 * @returns {boolean}
 */
function isFutureDate(dateStr) {
    const date = new Date(dateStr);
    const now = new Date();
    now.setHours(0, 0, 0, 0);
    date.setHours(0, 0, 0, 0);
    return date > now;
}

if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        formatDate,
        formatDateTime,
        localToISO,
        escapeHTML,
        showAlert,
        validateEmail,
        validateTelefono,
        isFutureDate
    };
}

/**
 * Convierte una fecha de input datetime-local a ISO string
 * @param {string} localDateTime - Valor de input datetime-local
 * @returns {string} - ISO string
 */
function localToISO(localDateTime) {
    // BUG INTENCIONAL: No especifica timezone, asume UTC
    // En Ecuador (GMT-5) hay una diferencia de 5 horas
    return new Date(localDateTime).toISOString();
}
