const {
    formatDate,
    formatDateTime,
    localToISO,
    escapeHTML,
    validateEmail,
    validateTelefono,
    isFutureDate
} = require('./utils.js');

describe('utils.js', () => {
    test('formatDate should format correctly', () => {
        expect(formatDate('2023-10-15')).toContain('2023');
    });

    test('escapeHTML should replace special characters', () => {
        expect(escapeHTML('<script>')).toBe('&lt;script&gt;');
        expect(escapeHTML('foo & bar')).toBe('foo &amp; bar');
    });

    test('validateEmail should return true for valid emails', () => {
        expect(validateEmail('test@test.com')).toBe(true);
        expect(validateEmail('invalid-email')).toBe(false);
    });

    test('validateTelefono should return true for valid formats', () => {
        expect(validateTelefono('0991234567')).toBe(true);
        expect(validateTelefono('123')).toBe(false);
    });

    test('isFutureDate should return true for future dates', () => {
        const future = new Date(Date.now() + 86400000).toISOString();
        expect(isFutureDate(future)).toBe(true);
        const past = new Date(Date.now() - 86400000).toISOString();
        expect(isFutureDate(past)).toBe(false);
    });
});
