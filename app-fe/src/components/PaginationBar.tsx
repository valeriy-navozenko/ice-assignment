'use client';

import {
  Box,
  FormControl,
  InputLabel,
  MenuItem,
  Pagination,
  Select,
  type SelectChangeEvent,
  Typography,
} from '@mui/material';
import { ChangeEvent } from 'react';
import { PAGE_SIZE_OPTIONS, PageParams } from '@/domain/page';

export type PaginationBarProps = Readonly<{
  page: number;
  pageSize: number;
  total: number;
  totalPages: number;
  onChange: (next: PageParams) => void;
}>;

/** Generic page navigation; emits the next `PageParams` to the parent on every change. */
export function PaginationBar({ page, pageSize, total, totalPages, onChange }: PaginationBarProps) {
  const handlePageChange = (_: ChangeEvent<unknown>, nextPage: number) => {
    if (nextPage !== page) onChange({ page: nextPage, pageSize });
  };

  const handlePageSizeChange = (event: SelectChangeEvent<number>) => {
    const nextPageSize = Number(event.target.value);
    if (nextPageSize !== pageSize) onChange({ page: 1, pageSize: nextPageSize });
  };

  const start = total === 0 ? 0 : (page - 1) * pageSize + 1;
  const end = Math.min(page * pageSize, total);

  return (
    <Box display="flex" alignItems="center" justifyContent="space-between" flexWrap="wrap" gap={2}>
      <Typography variant="body2" color="text.secondary" data-testid="pagination-summary">
        Showing {start}–{end} of {total}
      </Typography>
      <Box display="flex" alignItems="center" gap={2}>
        <FormControl size="small" sx={{ minWidth: 120 }}>
          <InputLabel id="pagination-page-size-label">Rows per page</InputLabel>
          <Select<number>
            labelId="pagination-page-size-label"
            label="Rows per page"
            value={pageSize}
            onChange={handlePageSizeChange}
            inputProps={{ 'data-testid': 'pagination-page-size-select' }}
          >
            {PAGE_SIZE_OPTIONS.map((size) => (
              <MenuItem key={size} value={size}>
                {size}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
        <Pagination
          page={page}
          count={totalPages}
          onChange={handlePageChange}
          color="primary"
          shape="rounded"
          data-testid="pagination"
        />
      </Box>
    </Box>
  );
}
