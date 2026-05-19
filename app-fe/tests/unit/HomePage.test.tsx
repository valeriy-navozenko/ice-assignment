import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import HomePage from '@/app/page';

const songsTableSpy = jest.fn();
jest.mock('@/components/SongsTable', () => ({
  SongsTable: () => {
    songsTableSpy();
    return <div data-testid="songs-table-stub" />;
  },
}));

afterEach(() => {
  songsTableSpy.mockClear();
});

describe('<HomePage />', () => {
  it('renders the heading and the songs table without passing any params', () => {
    render(<HomePage />);
    expect(screen.getByRole('heading', { name: /royalty progress/i })).toBeInTheDocument();
    expect(screen.getByTestId('songs-table-stub')).toBeInTheDocument();
    expect(songsTableSpy).toHaveBeenCalled();
  });
});
