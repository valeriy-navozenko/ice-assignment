import { DEFAULT_PAGE_SIZE, PageParams } from '@/domain/page';

const DEFAULT_PAGE = 1;

export class PaginationService {
  /** Apply defaults to `PageParams`, returning concrete page/pageSize values. */
  normalize(params: PageParams = {}): Required<PageParams> {
    return {
      page: params.page ?? DEFAULT_PAGE,
      pageSize: params.pageSize ?? DEFAULT_PAGE_SIZE,
    };
  }
}

export const paginationService = new PaginationService();
