/**
 * @jest-environment node
 */
import { HttpError, HttpService } from '@/services/HttpService';

function mockFetchOnce(overrides: Partial<Response> & { json?: () => Promise<unknown> }) {
  const mocked = jest.fn().mockResolvedValueOnce({
    ok: true,
    status: 200,
    statusText: 'OK',
    json: () => Promise.resolve({ ok: true }),
    ...overrides,
  } as Response);
  globalThis.fetch = mocked as unknown as typeof fetch;
  return mocked;
}

afterEach(() => {
  jest.restoreAllMocks();
  delete (globalThis as { fetch?: unknown }).fetch;
});

describe('HttpService.get', () => {
  it('calls fetch with the absolute URL and parses the JSON response', async () => {
    const fetchSpy = mockFetchOnce({ json: () => Promise.resolve({ value: 42 }) });
    const result = await new HttpService().get<{ value: number }>('http://example.test/foo?a=1');
    expect(fetchSpy).toHaveBeenCalledWith(
      'http://example.test/foo?a=1',
      expect.objectContaining({ next: { revalidate: 60 } }),
    );
    expect(result.value).toBe(42);
  });

  it('throws a typed HttpError when the response status is not ok', async () => {
    mockFetchOnce({ ok: false, status: 503, statusText: 'Service Unavailable' });
    await expect(new HttpService().get('http://example.test/foo')).rejects.toBeInstanceOf(
      HttpError,
    );
  });
});
