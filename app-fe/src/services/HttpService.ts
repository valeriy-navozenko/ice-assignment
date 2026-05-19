const DEFAULT_REVALIDATE_SECONDS = 60;
const DEFAULT_OPTIONS: RequestInit = { next: { revalidate: DEFAULT_REVALIDATE_SECONDS } };

export class HttpError extends Error {
  constructor(
    public readonly status: number,
    public readonly statusText: string,
    public readonly url: string,
  ) {
    super(`HTTP ${status} ${statusText} on ${url}`);
    this.name = 'HttpError';
  }
}

export class HttpService {
  /** Send a GET to the given URL and parse the JSON response. */
  async get<T>(url: string, options: RequestInit = DEFAULT_OPTIONS): Promise<T> {
    const response = await fetch(url, options);
    if (!response.ok) {
      throw new HttpError(response.status, response.statusText, url);
    }
    return response.json() as Promise<T>;
  }
}

export const httpService = new HttpService();
