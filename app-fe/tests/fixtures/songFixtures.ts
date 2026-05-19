import { Song } from '@/domain/song';

/** Named domain fixtures so individual specs stay declarative and intention-revealing. */
export const SongFixtures = {
  dieWithASmile: {
    id: 1,
    title: 'Die with a Smile',
    author: 'Lady Gaga, Bruno Mars',
    progress: 0.56,
  } satisfies Song,
  zeroProgress: {
    id: 5,
    title: 'Birds of a Feather',
    author: 'Billie Eilish',
    progress: 0,
  } satisfies Song,
  fullProgress: {
    id: 35,
    title: 'Worst Way',
    author: 'Riley Green',
    progress: 0.99,
  } satisfies Song,
  shortList: (): ReadonlyArray<Song> => [
    SongFixtures.dieWithASmile,
    SongFixtures.zeroProgress,
    SongFixtures.fullProgress,
  ],
};
