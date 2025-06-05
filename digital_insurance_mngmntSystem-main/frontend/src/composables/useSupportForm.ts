import { ref } from 'vue';
import { submitSupportTicket } from '../services/api';
import type { TicketData } from '../types/support';

export function useSupportForm() {
  const subject = ref('');
  const description = ref('');
  const loadingSubmit = ref(false);
  const error = ref('');
  const success = ref('');

  const resetForm = () => {
    subject.value = '';
    description.value = '';

  };

  const handleSubmitSupportTicket = async (ticketData: TicketData) => {
    loadingSubmit.value = true;
    error.value = '';
    success.value = '';
    try {
      await submitSupportTicket(ticketData);
      success.value = 'Support ticket submitted successfully! We will get back to you shortly.';
      resetForm();
      return true; 
    } catch (err) {
      let message = 'Failed to submit support ticket. Please try again.';
      if (err && typeof err === 'object') {
        const axiosError = err as { response?: { data?: any } };
        if (axiosError.response && axiosError.response.data) {
          if (typeof axiosError.response.data === 'string') {
            message = axiosError.response.data;
          } else if (axiosError.response.data.message && typeof axiosError.response.data.message === 'string') {
            message = axiosError.response.data.message;
          } else if (axiosError.response.data.error && typeof axiosError.response.data.error === 'string') {
            message = axiosError.response.data.error;
          }
        }
      }
      error.value = message;
      console.error('Submit error:', err);
      return false; 
    } finally {
      loadingSubmit.value = false;
    }
  };

  return {
    subject,
    description,
    loadingSubmit,
    error,
    success,
    resetForm,
    handleSubmitSupportTicket,
  };
}
