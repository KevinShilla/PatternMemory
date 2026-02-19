import { useMemo, useState } from "react";
import { useGameEngine } from "./game/useGameEngine";

function formatTime(ms: number) {
  const s = Math.floor(ms / 1000);
  const m = Math.floor(s / 60);
  const r = s % 60;
  return `${m}:${String(r).padStart(2, "0")}`;
}

export default function App() {
  const [gridSize, setGridSize] = useState(3);
  const engine = useGameEngine(gridSize);

  const isShowing = engine.mode === "showing";
  const isPlaying = engine.mode === "playing";
  const isOver = engine.mode === "gameover";

  const statusText = useMemo(() => {
    if (engine.mode === "idle") return "Press Start to begin.";
    if (engine.mode === "showing") return "Watch the pattern...";
    if (engine.mode === "playing") return "Your turn.";
    return "Game over. Press Start to try again.";
  }, [engine.mode]);

  return (
    <div className="wrap">
      <div className="card">
        <div className="header">
          <h1>Pattern Memory</h1>
          <div className="muted">{statusText}</div>
        </div>

        <div className="controls">
          <label>
            Grid
            <select value={gridSize} onChange={(e) => setGridSize(Number(e.target.value))}>
              <option value={3}>3 x 3</option>
              <option value={6}>6 x 6</option>
              <option value={12}>12 x 12</option>
            </select>
          </label>

          <div className="stats">
            <div>
              <div className="muted">Score</div>
              <div className="big">{engine.score}</div>
            </div>
            <div>
              <div className="muted">Time</div>
              <div className="big">{formatTime(engine.durationMs)}</div>
            </div>
          </div>

          <div className="buttons">
            <button className="btn" onClick={engine.start} disabled={isShowing || isPlaying}>
              Start
            </button>
            <button className="btn secondary" onClick={engine.reset} disabled={isShowing}>
              Reset
            </button>
          </div>
        </div>

        <GameBoard
          gridSize={gridSize}
          litIndex={engine.lit}
          disabled={!isPlaying}
          onClickSquare={engine.clickSquare}
        />

        {isOver && (
          <div className="note">
            You reached a score of <b>{engine.score}</b>.
          </div>
        )}
      </div>
    </div>
  );
}

function GameBoard(props: {
  gridSize: number;
  litIndex: number | null;
  disabled: boolean;
  onClickSquare: (idx: number) => void;
}) {
  const { gridSize, litIndex, disabled, onClickSquare } = props;
  const total = gridSize * gridSize;

  return (
    <div className="grid" style={{ gridTemplateColumns: `repeat(${gridSize}, 1fr)` }}>
      {Array.from({ length: total }).map((_, i) => {
        const lit = litIndex === i;
        return (
          <button
            key={i}
            className={`cell ${lit ? "lit" : ""}`}
            onClick={() => onClickSquare(i)}
            disabled={disabled}
            aria-label={`cell-${i}`}
          />
        );
      })}
    </div>
  );
}
