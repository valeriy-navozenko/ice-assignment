import { PaginationService } from '@/services/PaginationService';

describe('PaginationService.normalize', () => {
  it('applies defaults when both fields are missing', () => {
    expect(new PaginationService().normalize()).toEqual({ page: 1, pageSize: 50 });
  });

  it('preserves explicit page and pageSize values', () => {
    expect(new PaginationService().normalize({ page: 3, pageSize: 25 })).toEqual({
      page: 3,
      pageSize: 25,
    });
  });

  it('falls back to the default pageSize when only page is provided', () => {
    expect(new PaginationService().normalize({ page: 7 })).toEqual({ page: 7, pageSize: 50 });
  });
});
