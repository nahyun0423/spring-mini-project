import { useEffect, useMemo, useState } from "react";
import { NavLink, Route, Routes, useNavigate, useParams } from "react-router-dom";
import { api } from "./api";
import "./styles.css";

const pmLevels = ["GOOD", "NORMAL", "BAD", "VERY_BAD"];
const weatherTypes = ["SUNNY", "CLOUDY", "RAINY", "SNOWY", "UNKNOWN"];
const diseaseStatuses = ["NONE", "FLU_WARNING", "FLU_ALERT"];

const translate = {
  weatherType: {
    SUNNY: "맑음 ☀️",
    CLOUDY: "흐림 ☁️",
    RAINY: "비 ☔",
    SNOWY: "눈 ❄️",
    UNKNOWN: "알 수 없음",
  },
  pmLevel: {
    GOOD: "좋음 ✨",
    NORMAL: "보통 👍",
    BAD: "나쁨 😷",
    VERY_BAD: "매우나쁨 🚨",
  },
  disease: {
    NONE: "정상 ✅",
    FLU_WARNING: "독감 주의 ⚠️",
    FLU_ALERT: "독감 경보 🚨",
  },
};

function todayStr() {
  const d = new Date();
  const yyyy = d.getFullYear();
  const mm = String(d.getMonth() + 1).padStart(2, "0");
  const dd = String(d.getDate()).padStart(2, "0");
  return `${yyyy}-${mm}-${dd}`;
}

function Layout({ teacherId, setTeacherId, loading, msg, error }) {
  return (
    <div className="page">
      <header className="header">
        <div className="topRow">
          <div>
            <h1>Kindergarten Checklist</h1>
            <div className="sub">어린이집 변수 기반 업무 추천</div>
          </div>

          <label className="field small">
            <span>teacherId</span>
            <input
              type="number"
              value={teacherId}
              onChange={(e) => setTeacherId(Number(e.target.value))}
              min={1}
            />
          </label>
        </div>

        <nav className="nav">
          <NavLink to="/" end className={({ isActive }) => (isActive ? "active" : "")}>
            홈(오늘)
          </NavLink>
          <NavLink to="/manual" className={({ isActive }) => (isActive ? "active" : "")}>
            수동/자동 생성
          </NavLink>
          <NavLink to="/all" className={({ isActive }) => (isActive ? "active" : "")}>
            전체 목록
          </NavLink>
        </nav>

        {(msg || error) && (
          <div className={`toast ${error ? "err" : "ok"}`}>
            {error ? `❌ ${error}` : `✅ ${msg}`}
          </div>
        )}

        {loading && <div className="loadingBar" />}
      </header>

      <main className="container">
        <Routes>
          <Route path="/" element={<HomePage teacherId={teacherId} />} />
          <Route path="/manual" element={<ManualPage />} />
          <Route path="/all" element={<AllPage />} />
          <Route path="/checklists/:id" element={<ChecklistDetailPage teacherId={teacherId} />} />
          <Route path="*" element={<NotFound />} />
        </Routes>
      </main>

      <footer className="footer">
        <small className="muted">
        </small>
      </footer>
    </div>
  );
}

export default function App() {
  const [teacherId, setTeacherId] = useState(1);

  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState("");
  const [error, setError] = useState("");

  // ✅ 공통 run (초간단)
  window.__run = async (fn, okMessage = "성공!") => {
    setLoading(true);
    setMsg("");
    setError("");
    try {
      const result = await fn();
      setMsg(okMessage);
      return result;
    } catch (e) {
      setError(e?.message || "에러");
      throw e;
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout
      teacherId={teacherId}
      setTeacherId={setTeacherId}
      loading={loading}
      msg={msg}
      error={error}
    />
  );
}

/* ---------------- Pages ---------------- */

function HomePage({ teacherId }) {
  const run = window.__run;

  const [data, setData] = useState(null);
  const env = useMemo(() => data?.env, [data]);

  async function refresh() {
    const res = await run(() => api.getToday(), "오늘 체크리스트 조회 완료!");
    setData(res);
  }

  useEffect(() => {
    refresh();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <div className="grid2">
      <section className="card">
        <div className="row spaceBetween">
          <h2>오늘 체크리스트</h2>
          <button className="secondary" onClick={refresh}>
            새로고침
          </button>
        </div>

        {!data ? (
          <p className="muted">조회 결과가 없습니다.</p>
        ) : (
          <>
            <div className="summary" style={{ marginTop: 12 }}>
              <div>
                <div className="muted">기준 날짜</div>
                <div style={{ fontSize: 22, fontWeight: 900, letterSpacing: "-0.3px" }}>
                  {data.date}
                </div>
              </div>
            </div>

            <h3>오늘의 환경 정보</h3>
            {env ? (
              <div className="envBox">
                <div>
                  <span className="envLabel">날씨:</span>
                  <span className="envValue">
                    {translate.weatherType[env.weatherType] || env.weatherType}
                  </span>
                </div>
                <div>
                  <span className="envLabel">기온:</span>
                  <span className="envValue">{env.temperature ?? "-"}°C</span>
                </div>
                <div>
                  <span className="envLabel">미세먼지:</span>
                  <span className="envValue">{translate.pmLevel[env.pm10Level] || env.pm10Level}</span>
                </div>
                <div>
                  <span className="envLabel">초미세먼지:</span>
                  <span className="envValue">{translate.pmLevel[env.pm25Level] || env.pm25Level}</span>
                </div>
                <div
                  style={{
                    gridColumn: "span 2",
                    marginTop: "4px",
                    paddingTop: "8px",
                    borderTop: "1px solid #dee2e6",
                  }}
                >
                  <span className="envLabel">질병 주의보:</span>
                  <span
                    className="envValue"
                    style={{
                      color: env.diseaseStatus !== "NONE" ? "var(--error)" : "var(--accent)",
                    }}
                  >
                    {translate.disease[env.diseaseStatus] || env.diseaseStatus}
                  </span>
                </div>
              </div>
            ) : (
              <p className="muted">데이터를 불러오는 중...</p>
            )}
          </>
        )}
      </section>

      <section className="card">
        <div className="row spaceBetween">
          <h2>할 일</h2>
          <button className="secondary" onClick={refresh}>
            새로고침
          </button>
        </div>

        {!data?.items?.length ? (
          <p className="muted">할 일이 없습니다.</p>
        ) : (
          <ul className="list">
            {data.items.map((it) => (
              <li key={it.id} className={`item ${it.completed ? "done" : ""}`}>
                <div className="itemMain">
                  <div className="titleRow">
                    <span className="title">
                      {it.completed && <span className="checkIcon">✅ </span>}
                      {it.title}
                    </span>
                    <span className="badge">{it.category}</span>
                  </div>

                  <div className="desc">{it.description}</div>

                  {it.completed && (
                    <div className="doneTime">
                      완료:{" "}
                      {it.completedAt
                        ? new Date(it.completedAt).toLocaleTimeString("ko-KR", {
                            hour: "2-digit",
                            minute: "2-digit",
                          })
                        : "-"}
                    </div>
                  )}
                </div>

                <div className="itemActions">
                  <button
                    className={it.completed ? "buttonDone" : ""}
                    disabled={it.completed}
                    onClick={() =>
                      run(async () => {
                        await api.completeItem(it.id, teacherId);
                        await refresh();
                      }, "완료 처리됨!")
                    }
                  >
                    {it.completed ? "완료됨" : "완료"}
                  </button>
                </div>
              </li>
            ))}
          </ul>
        )}
      </section>
    </div>
  );
}

function ManualPage() {
  const run = window.__run;
  const navigate = useNavigate();

  const [autoDate, setAutoDate] = useState("");

  const [envForm, setEnvForm] = useState({
    date: "",
    weatherType: "SUNNY",
    temperature: 5,
    pm10Level: "NORMAL",
    pm25Level: "NORMAL",
    diseaseStatus: "NONE",
  });

  return (
    <div className="grid2">
      {/* ✅ 자동 생성 (원샷만 남김) */}
      <section className="card">
        <h2>EnvSnapshot 자동 생성</h2>

        <div className="formGrid">
          <label className="field">
            <span>date</span>
            <input
              value={autoDate}
              onChange={(e) => setAutoDate(e.target.value)}
              placeholder="YYYY-MM-DD"
            />
          </label>
        </div>

        <div className="row gap">
          <button
            className="secondary"
            onClick={() =>
              run(async () => {
                const date = autoDate?.trim() || null;

                // 1) 자동 EnvSnapshot 생성
                await api.createEnvSnapshotAuto(date);

                // 2) 체크리스트 생성 (date 기준)
                const gen = date
                  ? await api.regenerateByDate(date)
                  : await api.generateToday();

                // 3) 상세로 이동
                const checklistId = gen?.checklistId || gen?.id || gen;
                navigate(`/checklists/${checklistId}`);
              })
            }
          >
            EnvSnapshot 생성
          </button>
        </div>

        <p className="hint">
        </p>
      </section>

      {/* ✅ 수동 생성 (질병 상태 섹션 삭제) */}
      <section className="card">
        <h2>EnvSnapshot 수동 생성</h2>

        <div className="formGrid">
          <label className="field">
            <span>date</span>
            <input
              value={envForm.date}
              onChange={(e) => setEnvForm({ ...envForm, date: e.target.value })}
              placeholder="YYYY-MM-DD"
            />
          </label>

          <label className="field">
            <span>weatherType</span>
            <select
              value={envForm.weatherType}
              onChange={(e) => setEnvForm({ ...envForm, weatherType: e.target.value })}
            >
              {weatherTypes.map((w) => (
                <option key={w} value={w}>
                  {w}
                </option>
              ))}
            </select>
          </label>

          <label className="field">
            <span>temperature</span>
            <input
              type="number"
              value={envForm.temperature}
              onChange={(e) => setEnvForm({ ...envForm, temperature: Number(e.target.value) })}
            />
          </label>

          <label className="field">
            <span>pm10Level</span>
            <select
              value={envForm.pm10Level}
              onChange={(e) => setEnvForm({ ...envForm, pm10Level: e.target.value })}
            >
              {pmLevels.map((p) => (
                <option key={p} value={p}>
                  {p}
                </option>
              ))}
            </select>
          </label>

          <label className="field">
            <span>pm25Level</span>
            <select
              value={envForm.pm25Level}
              onChange={(e) => setEnvForm({ ...envForm, pm25Level: e.target.value })}
            >
              {pmLevels.map((p) => (
                <option key={p} value={p}>
                  {p}
                </option>
              ))}
            </select>
          </label>

          <label className="field">
            <span>diseaseStatus</span>
            <select
              value={envForm.diseaseStatus}
              onChange={(e) => setEnvForm({ ...envForm, diseaseStatus: e.target.value })}
            >
              {diseaseStatuses.map((d) => (
                <option key={d} value={d}>
                  {d}
                </option>
              ))}
            </select>
          </label>
        </div>

        <div className="row gap">
          <button
            onClick={() =>
              run(async () => {
                const payload = { ...envForm, date: envForm.date || null };

                // 1) 수동 EnvSnapshot 저장
                await api.createEnvSnapshot(payload);

                // 2) 체크리스트 생성 (date 기준)
                const targetDate = envForm.date?.trim();
                const gen = targetDate
                  ? await api.regenerateByDate(targetDate)
                  : await api.generateToday();

                // 3) 상세로 이동
                const checklistId = gen?.checklistId || gen?.id || gen;
                navigate(`/checklists/${checklistId}`);
              })
            }
          >
            EnvSnapshot 생성
          </button>
        </div>


      </section>
    </div>
  );
}


function AllPage() {
  const run = window.__run;
  const navigate = useNavigate();

  const [list, setList] = useState([]);

  async function load() {
    const res = await run(() => api.listAllChecklists(), "목록 조회 완료!");
    setList(res || []);
  }

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <div className="grid2">
      <section className="card">
        <div className="row spaceBetween">
          <h2>전체 체크리스트 목록</h2>
          <button className="secondary" onClick={load}>
            새로고침
          </button>
        </div>

        {!list?.length ? (
          <p className="muted">목록이 비어있습니다.</p>
        ) : (
          <ul className="list">
            {list.map((c) => (
              <li
                key={c.checklistId}
                className="item"
                style={{ cursor: "pointer" }}
                onClick={() => navigate(`/checklists/${c.checklistId}`)}
              >
                <div className="itemMain">
                  <div className="titleRow">
                    <span className="title">{c.date}</span>
                    <span className="badge">
                      {c.completedItems}/{c.totalItems}
                    </span>
                  </div>
                  <div className="meta">
                    <span>
                      id: <span className="mono">{c.checklistId}</span>
                    </span>
                  </div>
                </div>

                <div className="itemActions">
                  <button onClick={() => navigate(`/checklists/${c.checklistId}`)}>열기</button>
                </div>
              </li>
            ))}
          </ul>
        )}
      </section>

      <section className="card">
        <h2>안내</h2>
        <p className="muted">
          목록에서 항목을 누르면 <b>상세 페이지</b>로 이동합니다.
        </p>
      </section>
    </div>
  );
}

function ChecklistDetailPage({ teacherId }) {
  const run = window.__run;
  const { id } = useParams();
  const navigate = useNavigate();

  const [data, setData] = useState(null);
  const env = useMemo(() => data?.env, [data]);

  async function load() {
    const res = await run(() => api.getChecklistDetail(id), "상세 조회 완료!");
    setData(res);
  }

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  return (
    <div className="grid2">
      <section className="card">
        <div className="row spaceBetween">
          <h2>체크리스트 상세</h2>
          <div className="row gap">
            <button className="secondary" onClick={() => navigate(-1)}>
              뒤로
            </button>
            <button className="secondary" onClick={load}>
              새로고침
            </button>
          </div>
        </div>

        {!data ? (
          <p className="muted">상세 로딩 중...</p>
        ) : (
          <>
            <div className="summary" style={{ marginTop: 12 }}>
              <div>
                <div className="muted">기준 날짜</div>
                <div style={{ fontSize: 22, fontWeight: 900, letterSpacing: "-0.3px" }}>
                  {data.date}
                </div>
              </div>
            </div>

            <h3>환경 정보</h3>
            {env ? (
              <div className="envBox">
                <div>
                  <span className="envLabel">날씨:</span>
                  <span className="envValue">
                    {translate.weatherType[env.weatherType] || env.weatherType}
                  </span>
                </div>
                <div>
                  <span className="envLabel">기온:</span>
                  <span className="envValue">{env.temperature ?? "-"}°C</span>
                </div>
                <div>
                  <span className="envLabel">미세먼지:</span>
                  <span className="envValue">{translate.pmLevel[env.pm10Level] || env.pm10Level}</span>
                </div>
                <div>
                  <span className="envLabel">초미세먼지:</span>
                  <span className="envValue">{translate.pmLevel[env.pm25Level] || env.pm25Level}</span>
                </div>
                <div
                  style={{
                    gridColumn: "span 2",
                    marginTop: "4px",
                    paddingTop: "8px",
                    borderTop: "1px solid #dee2e6",
                  }}
                >
                  <span className="envLabel">질병 주의보:</span>
                  <span
                    className="envValue"
                    style={{
                      color: env.diseaseStatus !== "NONE" ? "var(--error)" : "var(--accent)",
                    }}
                  >
                    {translate.disease[env.diseaseStatus] || env.diseaseStatus}
                  </span>
                </div>
              </div>
            ) : (
              <p className="muted">env 없음</p>
            )}
          </>
        )}
      </section>

      <section className="card">
        <div className="row spaceBetween">
          <h2>할 일</h2>
          <button className="secondary" onClick={() => navigate("/")}>
            홈으로
          </button>
        </div>

        {!data?.items?.length ? (
          <p className="muted">할 일이 없습니다.</p>
        ) : (
          <ul className="list">
            {data.items.map((it) => (
              <li key={it.id} className={`item ${it.completed ? "done" : ""}`}>
                <div className="itemMain">
                  <div className="titleRow">
                    <span className="title">
                      {it.completed && <span className="checkIcon">✅ </span>}
                      {it.title}
                    </span>
                    <span className="badge">{it.category}</span>
                  </div>

                  <div className="desc">{it.description}</div>

                  {it.completed && (
                    <div className="doneTime">
                      완료:{" "}
                      {it.completedAt
                        ? new Date(it.completedAt).toLocaleTimeString("ko-KR", {
                            hour: "2-digit",
                            minute: "2-digit",
                          })
                        : "-"}
                    </div>
                  )}
                </div>

                <div className="itemActions">
                  <button
                    className={it.completed ? "buttonDone" : ""}
                    disabled={it.completed}
                    onClick={() =>
                      run(async () => {
                        await api.completeItem(it.id, teacherId);
                        await load();
                      }, "완료 처리됨!")
                    }
                  >
                    {it.completed ? "완료됨" : "완료"}
                  </button>
                </div>
              </li>
            ))}
          </ul>
        )}
      </section>
    </div>
  );
}

function NotFound() {
  const navigate = useNavigate();
  return (
    <section className="card">
      <h2>404</h2>
      <p className="muted">페이지가 없습니다.</p>
      <button className="secondary" onClick={() => navigate("/")}>
        홈으로
      </button>
    </section>
  );
}
