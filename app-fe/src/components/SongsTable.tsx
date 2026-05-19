'use client';

import {
  Box,
  CircularProgress,
  LinearProgress,
  Paper,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from '@mui/material';
import { useEffect, useState } from 'react';
import { Page, PageParams } from '@/domain/page';
import { Song } from '@/domain/song';
import { songService } from '@/services/SongService';
import { PaginationBar } from '@/components/PaginationBar';

/** Fetch a page of songs on mount and on every params change; re-renders without a page reload. */
export function SongsTable() {
  const [params, setParams] = useState<PageParams>({});
  const [result, setResult] = useState<Page<Song> | null>(null);

  useEffect(() => {
    let cancelled = false;
    songService.findPage(params).then((next) => {
      if (!cancelled) setResult(next);
    });
    return () => {
      cancelled = true;
    };
  }, [params]);

  if (!result) {
    return (
      <Box display="flex" justifyContent="center" py={6}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Stack spacing={2}>
      <TableContainer component={Paper} elevation={2}>
        <Table aria-label="songs table" size="small">
          <TableHead>
            <TableRow>
              <TableCell width={64}>ID</TableCell>
              <TableCell>Song Name</TableCell>
              <TableCell>Author</TableCell>
              <TableCell width={240}>Progress</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {result.items.map((song) => (
              <SongRow key={song.id} song={song} />
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      <PaginationBar
        page={result.page}
        pageSize={result.pageSize}
        total={result.total}
        totalPages={result.totalPages}
        onChange={setParams}
      />
    </Stack>
  );
}

type SongRowProps = Readonly<{ song: Song }>;

function SongRow({ song }: SongRowProps) {
  return (
    <TableRow hover data-testid={`song-row-${song.id}`}>
      <TableCell component="th" scope="row">
        {song.id}
      </TableCell>
      <TableCell>{song.title}</TableCell>
      <TableCell>{song.author}</TableCell>
      <TableCell>
        <ProgressCell progress={song.progress} />
      </TableCell>
    </TableRow>
  );
}

type ProgressCellProps = Readonly<{ progress: number }>;

function ProgressCell({ progress }: ProgressCellProps) {
  const clamped = clampProgress(progress);
  const label = `${Math.round(clamped * 100)}%`;
  return (
    <Box display="flex" alignItems="center" gap={1.5}>
      <Box flexGrow={1}>
        <LinearProgress
          variant="determinate"
          value={clamped * 100}
          aria-label={`royalty calculation progress ${label}`}
        />
      </Box>
      <Typography variant="body2" sx={{ minWidth: 40, textAlign: 'right' }}>
        {label}
      </Typography>
    </Box>
  );
}

/** Clamp to [0, 1]; NaN collapses to 0 to avoid `NaN%` rendering. */
function clampProgress(progress: number): number {
  if (Number.isNaN(progress)) return 0;
  return Math.max(0, Math.min(1, progress));
}
