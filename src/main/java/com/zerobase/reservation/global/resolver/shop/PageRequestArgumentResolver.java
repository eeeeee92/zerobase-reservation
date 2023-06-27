package com.zerobase.reservation.global.resolver.shop;

import com.zerobase.reservation.dto.Sort;
import com.zerobase.reservation.global.common.ParamToSortConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class PageRequestArgumentResolver implements HandlerMethodArgumentResolver {

    private final ParamToSortConverter converter;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        log.info("supportsParameter 실행");

        boolean hasDefaultPaging = parameter.hasParameterAnnotation(PageDefault.class);
        boolean hashPageRequestType = PageRequest.class.isAssignableFrom(parameter.getParameterType());
        return hasDefaultPaging && hashPageRequestType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        PageDefault annotation = parameter.getParameterAnnotation(PageDefault.class);
        PageRequest pageRequest = new PageRequest();

        String requestPageNum = request.getParameter("pageNum");
        String requestPageSize = request.getParameter("pageSize");
        String[] requestSorts = request.getParameterValues("sort");

        if (requestPageNum == null) {
            pageRequest.setPageNum(annotation.pageNum());
        } else {
            int pageNum = Integer.parseInt(requestPageNum);
            pageRequest.setPageSize(pageNum);

        }

        if (requestPageSize == null) {
            pageRequest.setPageSize(annotation.pageSize());
        } else {
            int pageSize = Integer.parseInt(requestPageSize);
            pageRequest.setPageSize(pageSize);
        }

        if (requestSorts == null) {
            if (annotation.sort().length > 0) {
                Arrays.stream(annotation.sort())
                        .map((columnNamesConstants) ->
                            Sort.builder()
                                    .property(columnNamesConstants.name())
                                    .direction(annotation.direction())
                                    .build()
                        ).forEach(pageRequest.getSort()::add);
            }
        } else {
            Arrays.stream(requestSorts)
                    .map(converter::convert)
                    .forEach(pageRequest.getSort()::add);
        }

        return pageRequest;
    }
}
