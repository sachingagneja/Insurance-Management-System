import { format, parseISO } from 'date-fns';

/**
 * Format a date string to a localized format
 * @param dateStr - ISO date string or undefined
 * @param formatStr - Format string (default: 'PP')
 * @returns Formatted date string or empty string if date is invalid
 */
export function formatDate(dateStr?: string, formatStr = 'PP'): string {
  if (!dateStr) return '';
  
  try {
    const date = parseISO(dateStr);
    return format(date, formatStr);
  } catch (error) {
    console.error('Error formatting date:', error);
    return '';
  }
}

/**
 * Get relative time (e.g., "2 days ago")
 * This is a placeholder function - to implement fully, you'd need the full date-fns library
 */
export function getRelativeTime(dateStr?: string): string {
  if (!dateStr) return '';
  
  // For now, just return the formatted date
  return formatDate(dateStr);
}