import '@testing-library/jest-dom';
import { fireEvent, render, screen } from '@testing-library/react';
import { PaginationBar } from '@/components/PaginationBar';

const baseProps = {
  page: 2,
  pageSize: 50,
  total: 100,
  totalPages: 2,
};

describe('<PaginationBar />', () => {
  it('renders the summary derived from page, pageSize, and total', () => {
    render(<PaginationBar {...baseProps} onChange={jest.fn()} />);
    expect(screen.getByTestId('pagination-summary')).toHaveTextContent('Showing 51–100 of 100');
  });

  it('emits the next PageParams when a different page is selected', () => {
    const onChange = jest.fn();
    render(<PaginationBar {...baseProps} page={1} totalPages={4} onChange={onChange} />);
    fireEvent.click(screen.getByRole('button', { name: /Go to page 2/i }));
    expect(onChange).toHaveBeenCalledWith({ page: 2, pageSize: 50 });
  });

  it('resets to page 1 when the page size changes', () => {
    const onChange = jest.fn();
    render(<PaginationBar {...baseProps} page={3} onChange={onChange} />);
    fireEvent.mouseDown(screen.getByLabelText(/Rows per page/i));
    fireEvent.click(screen.getByRole('option', { name: '25' }));
    expect(onChange).toHaveBeenCalledWith({ page: 1, pageSize: 25 });
  });

  it('renders zero start index when total is zero', () => {
    render(<PaginationBar {...baseProps} total={0} totalPages={1} page={1} onChange={jest.fn()} />);
    expect(screen.getByTestId('pagination-summary')).toHaveTextContent('Showing 0–0 of 0');
  });
});
