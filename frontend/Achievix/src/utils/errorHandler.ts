export function handleApiError(error: any): never {
  if (error.response && error.response.data) {
    console.error('Validation Error:', error.response.data.errors);
    throw new Error(JSON.stringify(error.response.data.errors));
  } else {
    console.error('Error:', error.message || error);
    throw error;
  }
}
