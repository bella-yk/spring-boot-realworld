package io.spring.common.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum HistoryAction {
    NEW("new"),
    EDIT("edit"),
    DELETE("delete");

    private String actionCode;

    HistoryAction(String actionCode ) {
        this.actionCode = actionCode;
    }

    public String getCode() {
        return this.actionCode;
    }
}
