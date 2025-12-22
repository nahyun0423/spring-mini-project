const BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

async function request(path, options = {}) {
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {}),
    },
    ...options,
  });

  if (res.status === 204) return null;

  const text = await res.text();
  const data = text ? safeJson(text) : null;

  if (!res.ok) {
    const msg = data?.message || data?.error || text || `HTTP ${res.status}`;
    throw new Error(msg);
  }
  return data;
}

function safeJson(text) {
  try {
    return JSON.parse(text);
  } catch {
    return { raw: text };
  }
}

export const api = {
  test: () => request("/test"),

  // ✅ EnvSnapshot: 수동 저장
  createEnvSnapshot: (body) =>
    request("/env-snapshots/manual", {
      method: "POST",
      body: JSON.stringify(body),
    }),

  // ✅ EnvSnapshot: 자동 생성
  createEnvSnapshotAuto: (date) =>
    request(`/env-snapshots/auto?date=${encodeURIComponent(date)}`, {
      method: "POST",
    }),

  // ✅ 체크리스트 생성
  generateToday: () =>
    request("/admin/checklists/generate-today", { method: "POST" }),

  regenerateByDate: (date) =>
    request(`/admin/checklists/regenerate?date=${encodeURIComponent(date)}`, {
      method: "POST",
    }),

  // ✅ 조회
  getToday: () => request("/checklists/today"),
  listAllChecklists: () => request("/checklists"),
  getChecklistDetail: (id) => request(`/checklists/${id}`),
  getByDate: (date) => request(`/checklists?date=${encodeURIComponent(date)}`),

  // ✅ 완료
  completeItem: (itemId, teacherId) =>
    request(`/checklists/items/${itemId}/complete`, {
      method: "POST",
      body: JSON.stringify({ teacherId }),
    }),
};
