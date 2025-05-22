import { Link } from 'react-router-dom';

export function NotFoundPage() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="max-w-md w-full px-6 py-12 bg-white shadow-md rounded-lg">
        <div className="text-center">
          <h1 className="text-6xl font-bold text-primary-600">404</h1>
          <h2 className="mt-4 text-2xl font-semibold text-gray-900">Page not found</h2>
          <p className="mt-2 text-gray-600">Sorry, we couldn't find the page you're looking for.</p>
          <Link
            to="/"
            className="mt-6 inline-block px-4 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700 transition-colors"
          >
            Go back home
          </Link>
        </div>
      </div>
    </div>
  );
}