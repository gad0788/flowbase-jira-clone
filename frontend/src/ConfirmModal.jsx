import { useState, useCallback } from 'react';

export function useConfirm() {
  const [state, setState] = useState(null);

  const confirm = useCallback((message, onConfirm) => {
    setState({ message, onConfirm });
  }, []);

  const handleConfirm = useCallback(() => {
    if (state) state.onConfirm();
    setState(null);
  }, [state]);

  const modal = state ? (
    <div className="modal-overlay" onClick={() => setState(null)}>
      <div className="modal-box" onClick={e => e.stopPropagation()}>
        <h3>Confirm</h3>
        <p>{state.message}</p>
        <div className="modal-actions">
          <button className="btn btn-secondary" onClick={() => setState(null)}>Cancel</button>
          <button className="btn btn-danger" onClick={handleConfirm}>Confirm</button>
        </div>
      </div>
    </div>
  ) : null;

  return { confirm, modal };
}
