export function SkeletonText({ width = '100%', height = 14, mb = 8 }) {
  return <div className="skeleton" style={{ height, width, marginBottom: mb }} />;
}

export function SkeletonCard() {
  return <div className="skeleton skeleton-card" />;
}

export function SkeletonAvatar({ size = 32 }) {
  return <div className="skeleton" style={{ width: size, height: size, borderRadius: '50%', flexShrink: 0 }} />;
}

export function SkeletonBoard() {
  return (
    <div className="board">
      {[1, 2, 3, 4, 5].map(i => (
        <div key={i} className="skeleton-column">
          <div className="skeleton skeleton-column-header" />
          <SkeletonCard />
          <SkeletonCard />
          <SkeletonCard />
        </div>
      ))}
    </div>
  );
}

export function SkeletonDetail() {
  return (
    <div>
      <div className="skeleton" style={{ height: 14, width: 200, marginBottom: 16 }} />
      <div className="skeleton" style={{ height: 28, width: '60%', marginBottom: 24 }} />
      <div className="detail-layout">
        <div className="detail-main">
          <div className="card">
            <div className="skeleton skeleton-text-lg" />
            <SkeletonText width="90%" />
            <SkeletonText width="75%" />
            <SkeletonText width="85%" />
            <SkeletonText width="40%" />
          </div>
        </div>
        <div className="detail-sidebar">
          <div className="card">
            <div className="skeleton skeleton-text-sm" />
            <SkeletonText width="70%" height={12} />
            <SkeletonText width="50%" height={12} />
            <SkeletonText width="60%" height={12} />
          </div>
        </div>
      </div>
    </div>
  );
}
