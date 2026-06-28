import { Component } from 'react';

export default class ErrorBoundary extends Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, info) {
    console.error('ErrorBoundary caught:', error, info);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="empty-state" style={{ padding: '80px 24px' }}>
          <div className="empty-icon" style={{ fontSize: 64 }}>⚠️</div>
          <h3>Something went wrong</h3>
          <p style={{ color: '#5e6c84', maxWidth: 400, margin: '0 auto 16px' }}>
            {this.state.error?.message || 'An unexpected error occurred.'}
          </p>
          <button className="btn btn-lg" onClick={() => { this.setState({ hasError: false, error: null }); window.location.href = '/'; }}>
            ← Back to Dashboard
          </button>
        </div>
      );
    }
    return this.props.children;
  }
}
