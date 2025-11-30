package com.kaif.companyms.companies;

public class CompanyConstants {
    private CompanyConstants() {}

    public static final String ID_PATH = "/{id}";
    public static final String LOGO_PATH = "/{id}/logo";
    public static final String DTO_ID_PATH = "/dto/{id}";
    public static final String SORTED_PATH = "/sorted/{field}";
    public static final String NAME_PATH = "/name/{name}";
    public static final String SEARCH_PATH = "/search";
    public static final String FILTER_YEAR_PATH = "/filterByYear/{year}";
    public static final String PAGINATION_PATH = "/pagination/{page}/{pageSize}";
}