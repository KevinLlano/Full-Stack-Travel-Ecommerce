// In production we serve the Angular bundle from the same Spring Boot container.
// Use a blank base so all API calls hit the same origin (Fly.io app domain) via relative paths.
export const environment = {
  production: true,
  URL: ''
};
