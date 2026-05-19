/**
 * @jest-environment node
 */
import { SongService } from '@/services/SongService';
import { HttpService } from '@/services/HttpService';
import { Page } from '@/domain/page';
import { Song } from '@/domain/song';

const samplePage: Page<Song> = {
  items: [{ id: 1, title: 'Die with a Smile', author: 'Lady Gaga, Bruno Mars', progress: 0.56 }],
  page: 1,
  pageSize: 25,
  total: 100,
  totalPages: 4,
};

describe('SongService.findPage', () => {
  it('delegates to the HttpService with the backend songs URL and params', async () => {
    const httpService = {
      get: jest.fn().mockResolvedValueOnce(samplePage),
    } as unknown as HttpService;
    const result = await new SongService(httpService).findPage({ page: 2, pageSize: 25 });
    expect(httpService.get).toHaveBeenCalledWith('http://localhost:8080/songs?page=2&pageSize=25');
    expect(result.items[0]?.title).toBe('Die with a Smile');
  });

  it('falls back to defaults when called with no params', async () => {
    const httpService = {
      get: jest.fn().mockResolvedValueOnce(samplePage),
    } as unknown as HttpService;
    await new SongService(httpService).findPage();
    expect(httpService.get).toHaveBeenCalledWith('http://localhost:8080/songs?page=1&pageSize=50');
  });
});
