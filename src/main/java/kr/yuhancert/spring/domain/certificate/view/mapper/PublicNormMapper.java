package kr.yuhancert.spring.domain.certificate.view.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kr.yuhancert.spring.domain.certificate.view.dto.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class PublicNormMapper {

    private final ObjectMapper om;

    public PublicNormMapper(ObjectMapper om) {
        this.om = om;
    }

    /* ---------- public builders ---------- */

    public CertificatePublicViewDto toPublic(JsonNode root) {
        return new CertificatePublicViewDto(
                toMeta(root),
                toSchedule(root, true),
                toExamInfo(root, true),
                toBasicInfo(root, true),
                toPreference(root, true)
        );
    }

    public ScheduleDto toSchedule(JsonNode root) {
        return toSchedule(root, false);
    }

    public ExamInfoDto toExamInfo(JsonNode root) {
        return toExamInfo(root, false);
    }

    public BasicInfoDto toBasicInfo(JsonNode root) {
        return toBasicInfo(root, false);
    }

    public PreferenceDto toPreference(JsonNode root) {
        return toPreference(root, false);
    }

    /* ---------- section mappers ---------- */
    /** Q-Net 형식: 각 아이템에서 여러 "기간" 문자열을 분석해 여러 이벤트 생성 */
    private List<ExamEventDto> mapQnetSchedule(JsonNode arr) {
        List<ExamEventDto> list = new ArrayList<>();
        for (JsonNode n : arr) {
            String phase = text(n, "phase", ""); // "필기" / "실기"
            boolean isDoc = phase.contains("필기");
            // 기간 문자열 → (start,end) 로 파싱
            Range reg      = parseRange(firstText(n, "접수기간"));
            Range addReg   = parseRange(firstText(n, "추가접수기간"));
            Range exam     = parseRange(firstText(n, "시험일"));
            Range result   = parseRange(firstText(n, "발표"));           // 보통 하루짜리
            Range objection= parseRange(firstText(n, "의견제시기간"));   // 있으면

            // 라벨/타입 매핑
            if (reg.valid()) {
                list.add(new ExamEventDto(reg.start, reg.end, "접수기간",
                        isDoc ? ExamEventType.DOC_REG : ExamEventType.PRAC_REG));
            }
            if (addReg.valid()) {
                list.add(new ExamEventDto(addReg.start, addReg.end, "추가접수",
                        isDoc ? ExamEventType.DOC_REG : ExamEventType.PRAC_REG));
            }
            if (exam.valid()) {
                list.add(new ExamEventDto(exam.start, exam.end, "시험일",
                        isDoc ? ExamEventType.DOC_EXAM : ExamEventType.PRAC_EXAM));
            }
            if (result.valid()) {
                list.add(new ExamEventDto(result.start, result.end, "합격발표",
                        isDoc ? ExamEventType.DOC_PASS : ExamEventType.PRAC_PASS));
            }
            if (objection.valid()) {
                list.add(new ExamEventDto(objection.start, objection.end, "의견제시기간",
                        isDoc ? ExamEventType.DOC_PASS : ExamEventType.PRAC_PASS));
            }
        }
        return list;
    }


    private ScheduleDto toSchedule(JsonNode root, boolean includeMeta) {
        // 1) 우리가 정의한 배열형(이미 구현돼 있음)
        JsonNode node = findAny(root,
                "tabs.exam_schedule", "tabs.schedule", "tabs.시험일정", "시험일정"
        );
        List<ExamEventDto> events = new ArrayList<>();

        // 1-1) 기존 배열형(events/list/items)을 우선 시도
        JsonNode arr = node;
        if (arr != null && arr.isObject()) {
            arr = findAny(arr, "events", "list", "items");
        }
        if (arr != null && arr.isArray()) {
            events.addAll(mapEvents(arr));
        }

        // 2) Q-Net 스타일: 시험일정.정기검정일정[ {...} ]
        // (루트/어딘가에 있을 수 있으니 직접 탐색)
        JsonNode qnet = findAny(root, "시험일정.정기검정일정", "tabs.시험일정.정기검정일정");
        if (qnet != null && qnet.isArray()) {
            events.addAll(mapQnetSchedule(qnet));
        }

        return new ScheduleDto(events, includeMeta ? toMeta(root) : null);
    }



    private ExamInfoDto toExamInfo(JsonNode root, boolean includeMeta) {
        JsonNode obj = findAny(root, "tabs.exam_info", "시험정보");
        List<InfoSectionDto> sections = mapSections(obj);
        return new ExamInfoDto(sections, includeMeta ? toMeta(root) : null);
    }

    private BasicInfoDto toBasicInfo(JsonNode root, boolean includeMeta) {
        JsonNode base = findAny(root, "tabs.basic_info", "기본정보");
        String overview = textAt(base, "overview", "개요");
        List<HistoryItemDto> history = mapHistory(findAny(base, "history", "변천과정"));
        return new BasicInfoDto(overview, history, includeMeta ? toMeta(root) : null);
    }

    private PreferenceDto toPreference(JsonNode root, boolean includeMeta) {
        JsonNode obj = findAny(root, "tabs.preference", "우대현황");
        List<InfoSectionDto> sections = mapSections(obj);
        return new PreferenceDto(sections, includeMeta ? toMeta(root) : null);
    }

    /* ---------- meta ---------- */

    private PublicMetaDto toMeta(JsonNode root) {
        JsonNode meta = root.path("_meta");
        return new PublicMetaDto(
                text(meta, "schema", "public_norm_v1"),
                text(meta, "schema_version", "v1"),
                text(meta, "generated_at", null),
                text(meta, "jmcd", null),
                text(meta, "name", null)
        );
    }

    /* ---------- helpers ---------- */
    private String firstText(JsonNode n, String... keys) {
        if (n == null) return null;
        for (String k : keys) {
            JsonNode v = n.get(k);
            if (v != null && v.isValueNode() && !v.isNull()) return v.asText();
        }
        return null;
    }

    private static class Range {
        final LocalDate start, end;
        Range(LocalDate s, LocalDate e){ this.start=s; this.end=e; }
        boolean valid(){ return start != null || end != null; }
    }

    private Range parseRange(String s) {
        if (s == null || s.isBlank()) return new Range(null, null);
        // "2025.01.06 ~ 2025.01.09" / "2025.02.08" 둘 다 허용
        String[] parts = s.split("~");
        DateTimeFormatter[] fmts = new DateTimeFormatter[]{
                DateTimeFormatter.ofPattern("yyyy.MM.dd"),
                DateTimeFormatter.ISO_LOCAL_DATE
        };
        LocalDate a = parseDate(parts[0].trim(), fmts);
        LocalDate b = (parts.length > 1) ? parseDate(parts[1].trim(), fmts) : a;
        return new Range(a, b);
    }


    /** events 배열 → ExamEventDto[] */
    private List<ExamEventDto> mapEvents(JsonNode arrNode) {
        if (arrNode == null || !arrNode.isArray()) return List.of();

        DateTimeFormatter[] fmts = new DateTimeFormatter[] {
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ofPattern("yyyy.MM.dd"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd")
        };

        return StreamSupport.stream(arrNode.spliterator(), false)
                .map(n -> {
                    String sdStr = firstText(n, "startdate","startDate","start_date","start","from");
                    String edStr = firstText(n, "enddate","endDate","end_date","end","to");
                    String label  = firstText(n, "label","title","name");
                    String typeStr= firstText(n, "type","kind","category");

                    LocalDate sd = parseDate(sdStr, fmts);
                    LocalDate ed = parseDate(edStr, fmts);

                    ExamEventType type = ExamEventType.DOC_EXAM;
                    if (typeStr != null) {
                        try {
                            type = ExamEventType.valueOf(typeStr.toUpperCase(Locale.ROOT).replace('-', '_'));
                        } catch (Exception ignore) {}
                    }
                    return new ExamEventDto(sd, ed, label, type);
                })
                .collect(Collectors.toList());
    }

    /** 섹션 오브젝트 → (key,title,html) 리스트 */
    private List<InfoSectionDto> mapSections(JsonNode obj) {
        if (obj == null || !obj.isObject()) return List.of();

        List<InfoSectionDto> list = new ArrayList<>();
        Iterator<Entry<String, JsonNode>> it = obj.fields();
        while (it.hasNext()) {
            Entry<String, JsonNode> e = it.next();
            String key = e.getKey();              // 내부 키
            JsonNode v = e.getValue();
            // 값이 문자열/HTML 이면 그대로, 오브젝트/배열이면 prettify해서 문자열화
            String html;
            if (v.isTextual()) {
                html = v.asText();
            } else if (v.isArray() || v.isObject()) {
                html = v.toPrettyString();
            } else {
                html = String.valueOf(v);
            }
            // 화면 타이틀은 기본적으로 key와 동일, 한글키면 그대로 사용
            list.add(new InfoSectionDto(key, key, html));
        }
        return list;
    }

    /** 변천과정 배열 → HistoryItemDto[] */
    private List<HistoryItemDto> mapHistory(JsonNode arr) {
        if (arr == null || !arr.isArray()) return List.of();
        List<HistoryItemDto> list = new ArrayList<>();
        for (JsonNode n : arr) {
            list.add(new HistoryItemDto(
                    text(n, "date", null),
                    text(n, "title", null),
                    text(n, "law", null),
                    // 데이터에 raw_top / rawTop 등 혼재 가능성
                    text(n, "raw_top", text(n, "rawTop", null))
            ));
        }
        return list;
    }

    /** 다국어/대체 경로 지원: "a.b.c" 형태 path들 중 처음 존재하는 노드 반환 */
    private JsonNode findAny(JsonNode root, String... paths) {
        for (String p : paths) {
            JsonNode n = byPath(root, p);
            if (n != null && !n.isMissingNode() && !n.isNull()) return n;
        }
        return null;
    }

    private String textAt(JsonNode base, String... keys) {
        if (base == null) return null;
        for (String k : keys) {
            JsonNode n = base.get(k);
            if (n != null && !n.isNull() && n.isValueNode()) return n.asText();
        }
        return null;
    }

    private String text(JsonNode n, String key, String def) {
        if (n == null) return def;
        JsonNode v = n.get(key);
        return (v != null && v.isValueNode()) ? v.asText() : def;
    }

    private JsonNode byPath(JsonNode root, String dotted) {
        if (root == null || dotted == null || dotted.isBlank()) return null;
        JsonNode cur = root;
        for (String seg : dotted.split("\\.")) {
            if (!(cur instanceof ObjectNode) && !cur.isObject()) return null;
            cur = cur.get(seg);
            if (cur == null) return null;
        }
        return cur;
    }

    private LocalDate parseDate(String s, DateTimeFormatter[] fmts) {
        if (s == null || s.isBlank()) return null;
        for (DateTimeFormatter f : fmts) {
            try { return LocalDate.parse(s.trim(), f); } catch (Exception ignore) {}
        }
        // yyyy-MM-dd HH:mm 같은 경우 앞 10자리 자르기
        if (s.length() >= 10) {
            try { return LocalDate.parse(s.substring(0, 10), DateTimeFormatter.ISO_LOCAL_DATE); } catch (Exception ignore) {}
        }
        return null;
    }
}
