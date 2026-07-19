const { test, expect } = require('@playwright/test');

test.describe('Hospital Management E2E', () => {
    test('Debería cargar la página principal', async ({ page }) => {
        await page.goto('/');
        await expect(page).toHaveTitle(/Hospital Management System/);
        const header = await page.locator('header h1');
        await expect(header).toBeVisible();
    });

    test('Debería navegar a la sección de doctores', async ({ page }) => {
        await page.goto('/');
        await page.click('button[data-section="doctores"]');
        const h2 = await page.locator('#section-doctores h2');
        await expect(h2).toHaveText('Doctores');
    });

    test('Debería poder crear un nuevo doctor (mockeado)', async ({ page }) => {
        // Intercept API call to return a mock response for creating a doctor
        await page.route('http://localhost:8080/api/doctores', async (route) => {
            if (route.request().method() === 'POST') {
                await route.fulfill({
                    status: 201,
                    contentType: 'application/json',
                    body: JSON.stringify({
                        id: 99,
                        nombre: 'Test',
                        apellido: 'Doctor',
                        especialidad: 'General',
                        email: 'test@doctor.com',
                        telefono: '0999999999',
                        consultorio: '101'
                    })
                });
            } else if (route.request().method() === 'GET') {
                // Return a mock list including our new doctor
                await route.fulfill({
                    status: 200,
                    contentType: 'application/json',
                    body: JSON.stringify([{
                        id: 99,
                        nombre: 'Test',
                        apellido: 'Doctor',
                        especialidad: 'General',
                        email: 'test@doctor.com',
                        telefono: '0999999999',
                        consultorio: '101'
                    }])
                });
            } else {
                await route.continue();
            }
        });

        await page.goto('/');
        await page.click('button[data-section="doctores"]');
        
        // Clic en nuevo doctor
        await page.click('#btn-nuevo-doctor');
        
        // Llenar formulario
        await page.fill('#doctor-nombre', 'Test');
        await page.fill('#doctor-apellido', 'Doctor');
        await page.fill('#doctor-especialidad', 'General');
        await page.fill('#doctor-email', 'test@doctor.com');
        
        // Guardar
        await page.click('#doctor-form button[type="submit"]');
        
        // Verificar que aparece en la tabla
        const tableContent = await page.locator('#doctores-table').textContent();
        expect(tableContent).toContain('Test Doctor');
    });
});
