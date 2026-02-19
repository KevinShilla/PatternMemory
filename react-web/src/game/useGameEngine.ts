import { useEffect, useRef, useState } from "react";

function sleep(ms: number) {
  return new Promise((r) => setTimeout(r, ms));
}

type Mode = "idle" | "showing" | "playing" | "gameover";

export function useGameEngine(gridSize: number) {
  const total = gridSize * gridSize;

  const [mode, setMode] = useState<Mode>("idle");
  const [sequence, setSequence] = useState<number[]>([]);
  const [round, setRound] = useState(1);
  const [step, setStep] = useState(0);
  const [lit, setLit] = useState<number | null>(null);

  const startTimeRef = useRef<number | null>(null);
  const [durationMs, setDurationMs] = useState(0);

  useEffect(() => {
    let timer: number | undefined;

    if (mode === "playing" || mode === "showing") {
      timer = window.setInterval(() => {
        if (startTimeRef.current != null) {
          setDurationMs(Date.now() - startTimeRef.current);
        }
      }, 200);
    }

    return () => {
      if (timer) window.clearInterval(timer);
    };
  }, [mode]);

  function reset() {
    setMode("idle");
    setSequence([]);
    setRound(1);
    setStep(0);
    setLit(null);
    setDurationMs(0);
    startTimeRef.current = null;
  }

  function nextSquare(prevLast: number | null) {
    let n = Math.floor(Math.random() * total);
    while (prevLast != null && n === prevLast) n = Math.floor(Math.random() * total);
    return n;
  }

  async function showSequence(seq: number[]) {
    setMode("showing");
    for (const idx of seq) {
      setLit(idx);
      await sleep(250);
      setLit(null);
      await sleep(250);
    }
    setMode("playing");
  }

  async function start() {
    reset();
    startTimeRef.current = Date.now();

    const first = nextSquare(null);
    const seq = [first];

    setSequence(seq);
    setRound(1);
    setStep(0);

    await sleep(150);
    await showSequence(seq);
  }

  async function advanceRound() {
    const last = sequence.length ? sequence[sequence.length - 1] : null;
    const seq = [...sequence, nextSquare(last)];

    setSequence(seq);
    setRound((r) => r + 1);
    setStep(0);

    await sleep(350);
    await showSequence(seq);
  }

  async function clickSquare(index: number) {
    if (mode !== "playing") return;

    setLit(index);
    await sleep(120);
    setLit(null);

    const expected = sequence[step];
    if (index !== expected) {
      setMode("gameover");
      return;
    }

    const nextStep = step + 1;
    setStep(nextStep);

    if (nextStep === round) {
      if (round >= total) {
        setMode("gameover");
        return;
      }
      await advanceRound();
    }
  }

  useEffect(() => {
    reset();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [gridSize]);

  const score = Math.max(0, round - 1);

  return {
    mode,
    lit,
    score,
    durationMs,
    start,
    reset,
    clickSquare
  };
}
