export default function ShortcutsHelp({ onClose }) {
  const kbd = {
    display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
    minWidth: 28, height: 28, padding: '0 6px',
    background: '#f4f5f7', border: '1px solid #dfe1e6',
    borderRadius: 4, fontSize: 12, fontWeight: 600,
    fontFamily: 'monospace',
  };

  return (
    <div
      style={{
        position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.5)',
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        zIndex: 1000,
      }}
      onClick={onClose}
    >
      <div
        style={{
          background: '#fff', borderRadius: 8, padding: 24, minWidth: 320,
          maxWidth: 420, boxShadow: '0 8px 32px rgba(0,0,0,0.3)',
        }}
        onClick={e => e.stopPropagation()}
      >
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
          <h2 style={{ margin: 0, fontSize: 18, fontWeight: 600 }}>Keyboard Shortcuts</h2>
          <button
            onClick={onClose}
            style={{ background: 'none', border: 'none', fontSize: 18, cursor: 'pointer', padding: '4px 8px', borderRadius: 4 }}
          >✕</button>
        </div>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
          <ShortcutRow kbdStyle={kbd} keys={['?']} desc="Show/hide shortcuts" />
          <ShortcutRow kbdStyle={kbd} keys={['n']} desc="New issue" />
          <ShortcutRow kbdStyle={kbd} keys={['b']} desc="Board view" />
          <ShortcutRow kbdStyle={kbd} keys={['g', 'd']} desc="Dashboard" chord />
          <ShortcutRow kbdStyle={kbd} keys={['⌘K']} desc="Quick switcher" />
          <ShortcutRow kbdStyle={kbd} keys={['Esc']} desc="Close" />
        </div>
      </div>
    </div>
  );
}

function ShortcutRow({ kbdStyle, keys, desc, chord }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
      {keys.map((k, i) => (
        <span key={i} style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
          <kbd style={kbdStyle}>{k}</kbd>
          {chord && i < keys.length - 1 && (
            <span style={{ fontSize: 12, color: '#8993a4' }}>then</span>
          )}
        </span>
      ))}
      <span style={{ color: '#172b4d' }}>{desc}</span>
    </div>
  );
}
