export const DEFAULT_PAGE_SIZE = 50;

/** Suggested page-size choices shown in the UI selector. */
export const PAGE_SIZE_OPTIONS: ReadonlyArray<number> = [25, 50, 100];

/** Pagination request input; fields optional, defaults applied by the consumer. */
export interface PageParams {
  page?: number;
  pageSize?: number;
}

export interface Page<T> {
  readonly items: ReadonlyArray<T>;
  readonly page: number;
  readonly pageSize: number;
  readonly total: number;
  readonly totalPages: number;
}
