const { apiFetch } = require('./api.js');

describe('api.js', () => {
    beforeEach(() => {
        global.fetch = jest.fn();
    });

    afterEach(() => {
        jest.clearAllMocks();
    });

    test('apiFetch should call fetch with timeout and handle success', async () => {
        global.fetch.mockResolvedValueOnce({
            ok: true,
            status: 200,
            json: async () => ({ id: 1, name: 'Test' })
        });

        const data = await apiFetch('http://test.com/api', { method: 'GET' });
        expect(data).toEqual({ id: 1, name: 'Test' });
        expect(global.fetch).toHaveBeenCalledTimes(1);
    });

    test('apiFetch should handle 204 No Content', async () => {
        global.fetch.mockResolvedValueOnce({
            ok: true,
            status: 204
        });

        const data = await apiFetch('http://test.com/api', { method: 'DELETE' });
        expect(data).toBeNull();
    });

    test('apiFetch should throw error on bad response', async () => {
        global.fetch.mockResolvedValueOnce({
            ok: false,
            status: 400,
            json: async () => ({ message: 'Bad request error' })
        });

        await expect(apiFetch('http://test.com/api', { method: 'GET' })).rejects.toThrow('Bad request error');
    });
});
