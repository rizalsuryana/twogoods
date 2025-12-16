package com.finpro.twogoods.dto.response;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagedResult<T> {
	private List<T> data;
	private PagingResponse paging;
}
