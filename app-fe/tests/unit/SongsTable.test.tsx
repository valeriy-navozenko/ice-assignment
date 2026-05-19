import '@testing-library/jest-dom';
import { render, screen, waitFor } from '@testing-library/react';
import { SongsTable } from '@/components/SongsTable';
import { Page } from '@/domain/page';
import { Song } from '@/domain/song';
import { songService } from '@/services/SongService';
import { SongFixtures } from '../fixtures/songFixtures';

function pageFromFixture(items: ReadonlyArray<Song>): Page<Song> {
  return { items, page: 1, pageSize: 25, total: items.length, totalPages: 1 };
}

afterEach(() => {
  jest.restoreAllMocks();
});

describe('<SongsTable />', () => {
  it('fetches songs on mount and renders the canonical columns and rows', async () => {
    jest
      .spyOn(songService, 'findPage')
      .mockResolvedValueOnce(pageFromFixture(SongFixtures.shortList()));
    render(<SongsTable />);
    await waitFor(() => expect(screen.getByText('Die with a Smile')).toBeInTheDocument());
    expect(screen.getByRole('table', { name: /songs table/i })).toBeInTheDocument();
    expect(screen.getByText('ID')).toBeInTheDocument();
    expect(screen.getByText('Song Name')).toBeInTheDocument();
    expect(screen.getByText('Author')).toBeInTheDocument();
    expect(screen.getByText('Progress')).toBeInTheDocument();
    expect(screen.getByTestId('song-row-1')).toBeInTheDocument();
    expect(screen.getByTestId('song-row-5')).toBeInTheDocument();
    expect(screen.getByTestId('song-row-35')).toBeInTheDocument();
  });

  it('renders progress as a percent label with an accessible aria-label', async () => {
    jest
      .spyOn(songService, 'findPage')
      .mockResolvedValueOnce(pageFromFixture(SongFixtures.shortList()));
    render(<SongsTable />);
    await waitFor(() => expect(screen.getByText('56%')).toBeInTheDocument());
    expect(screen.getByLabelText(/royalty calculation progress 99%/i)).toBeInTheDocument();
  });
});
