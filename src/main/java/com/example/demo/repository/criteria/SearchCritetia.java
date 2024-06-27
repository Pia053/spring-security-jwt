package com.example.demo.repository.criteria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchCritetia {
    private String key; // firstName, lastName, id, email.
    private String operation;     // toán tử: > < =
    private Object value;
}
