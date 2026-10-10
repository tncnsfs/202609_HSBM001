// 서비스 항목 데이터 모양 (백엔드 ServiceItem과 같은 필드)
// export = 다른 파일에서 import 해서 쓸 수 있게 내보냄
export interface ServiceItem {
  id: number
  name: string
  category: string
  price: number
}

// 등록/수정할 때 보내는 값 (id는 백엔드가 정하므로 뺌)
// Omit<타입, '필드'> = 그 타입에서 해당 필드만 뺀 새 타입
export type ServiceItemInput = Omit<ServiceItem, 'id'>

// 모든 API 주소의 앞부분. 바뀌면 여기만 고치면 됨
const BASE = '/api/items'

// 목록 조회: GET /api/items
export async function fetchItems (): Promise<ServiceItem[]> {
  const res = await fetch(BASE)
  // res.ok = 응답 코드가 200번대인지. 아니면 에러를 던져서 화면에서 알 수 있게 함
  if (!res.ok) throw new Error(`목록 조회 실패: ${res.status}`)
  return res.json()
}

// 등록: POST /api/items
export async function createItem (input: ServiceItemInput): Promise<ServiceItem> {
  const res = await fetch(BASE, {
    method: 'POST',
    // 보내는 내용이 JSON이라고 알려 줌
    headers: { 'Content-Type': 'application/json' },
    // 객체를 JSON 글자로 바꿔 보냄
    body: JSON.stringify(input),
  })
  if (!res.ok) throw new Error(`등록 실패: ${res.status}`)
  return res.json()
}

// 수정: PUT /api/items/{id}
export async function updateItem (id: number, input: ServiceItemInput): Promise<ServiceItem> {
  const res = await fetch(`${BASE}/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(input),
  })
  if (!res.ok) throw new Error(`수정 실패: ${res.status}`)
  return res.json()
}

// 삭제: DELETE /api/items/{id}  (백엔드는 204 = 내용 없음으로 응답)
export async function deleteItem (id: number): Promise<void> {
  const res = await fetch(`${BASE}/${id}`, { method: 'DELETE' })
  if (!res.ok) throw new Error(`삭제 실패: ${res.status}`)
}