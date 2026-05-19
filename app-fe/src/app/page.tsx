import { Container, Stack, Typography } from '@mui/material';
import { SongsTable } from '@/components/SongsTable';

export default function HomePage() {
  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Stack spacing={3}>
        <Typography variant="h4" component="h1">
          Royalty Progress
        </Typography>
        <SongsTable />
      </Stack>
    </Container>
  );
}
