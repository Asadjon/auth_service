package org.cyber_pantera.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    ADMIN,

    MANAGER,

    INSPECTOR,

    USER
}
