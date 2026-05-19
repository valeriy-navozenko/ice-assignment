'use client';

import { createTheme } from '@mui/material/styles';
import { Roboto } from 'next/font/google';

const roboto = Roboto({
  weight: ['300', '400', '500', '700'],
  subsets: ['latin'],
  display: 'swap',
});

const theme = createTheme({
  palette: {
    mode: 'light',
    primary: { main: '#1976d2' },
    secondary: { main: '#9c27b0' },
    background: { default: '#f5f7fa' },
  },
  typography: {
    fontFamily: roboto.style.fontFamily,
  },
  components: {
    MuiTableCell: {
      styleOverrides: {
        head: { fontWeight: 600, backgroundColor: '#fafafa' },
      },
    },
  },
});

export default theme;
