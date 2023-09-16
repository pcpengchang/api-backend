package com.pc.gateway;

import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pengchang
 * @date 2023/09/06 11:02
 **/
public class MyRouteDefinition {
    private String id;

    private List<MyFilterDefinition> filters = new ArrayList<>();

    private List<MyPredicateDefinition> predicates = new ArrayList<>();

    private String uri;

    private int order = 0;

    public RouteDefinition getRouteDefinition(){
        RouteDefinition definition = new RouteDefinition();
        definition.setId(this.getId());
        definition.setOrder(this.getOrder());

        //设置断言
        List<PredicateDefinition> pdList = new ArrayList<>();
        List<MyPredicateDefinition> myPredicateDefinitionList = this.getPredicates();
        for (MyPredicateDefinition gpDefinition: myPredicateDefinitionList) {
            PredicateDefinition predicate = new PredicateDefinition();
            predicate.setArgs(gpDefinition.getArgs());
            predicate.setName(gpDefinition.getName());
            pdList.add(predicate);
        }

        definition.setPredicates(pdList);

        //设置过滤器
        List<FilterDefinition> filters = new ArrayList();
        List<MyFilterDefinition> gatewayFilters = this.getFilters();
        for(MyFilterDefinition filterDefinition : gatewayFilters){
            FilterDefinition filter = new FilterDefinition();
            filter.setName(filterDefinition.getName());
            filter.setArgs(filterDefinition.getArgs());
            filters.add(filter);
        }
        definition.setFilters(filters);

        URI uri = null;
        if(this.getUri().startsWith("http")){
            uri = UriComponentsBuilder.fromHttpUrl(this.getUri()).build().toUri();
        }else{
            uri = URI.create(this.getUri());
        }
        definition.setUri(uri);
        return definition;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<MyFilterDefinition> getFilters() {
        return filters;
    }

    public void setFilters(List<MyFilterDefinition> filters) {
        this.filters = filters;
    }

    public List<MyPredicateDefinition> getPredicates() {
        return predicates;
    }

    public void setPredicates(List<MyPredicateDefinition> predicates) {
        this.predicates = predicates;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
