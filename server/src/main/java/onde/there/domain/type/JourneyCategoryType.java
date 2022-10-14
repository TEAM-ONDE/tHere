package onde.there.domain.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum JourneyCategoryType {
    HEALING("힐링"),
    RESTAURANT("식도락"),
    LEISURE("레저"),
    CITY("도시"),
    CULTURE("문화"),
    CAMPING("캠핑"),
    KIDS("키즈"),
    COST_EFFECTIVENESS("가성비"),
    PET("반려동물"),
    ECT("기타"),
    ;
    private final String description;
}
