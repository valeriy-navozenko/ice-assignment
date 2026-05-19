import { Page, PageParams } from '@/domain/page';
import { Song } from '@/domain/song';
import { BACKEND_URL } from '@/lib/config';
import { HttpService, httpService as defaultHttpService } from '@/services/HttpService';
import {
  PaginationService,
  paginationService as defaultPaginationService,
} from '@/services/PaginationService';

const SONGS_URL = `${BACKEND_URL}/songs`;

export class SongService {
  constructor(
    private readonly httpService: HttpService = defaultHttpService,
    private readonly paginationService: PaginationService = defaultPaginationService,
  ) {}

  /** Get list of songs for page. */
  findPage(params: PageParams = {}): Promise<Page<Song>> {
    const { page, pageSize } = this.paginationService.normalize(params);
    return this.httpService.get<Page<Song>>(`${SONGS_URL}?page=${page}&pageSize=${pageSize}`);
  }
}

export const songService = new SongService();
