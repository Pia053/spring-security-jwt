package com.example.demo.repository;


import com.example.demo.dto.response.PageResponse;
import com.example.demo.model.Address;
import com.example.demo.model.User;
import com.example.demo.repository.criteria.SearchCritetia;
import com.example.demo.repository.criteria.UserSearchQueryCriteriaConsumer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class SearchRepository {

    @PersistenceContext
    private EntityManager entityManager;
//  StringBuilder: không bất biến có thể thay đỏi giá trị, không đồng bộ đơn luồng, 0 an toàn đa luồng
//  StringBuffer: Không bất biến có thể thay đổi giá trị, có đồng bộ đa luồng, an toàn đa luồng

    public PageResponse<?> getAllUserWithSortByMultipleColumnsAndSearch(int pageNo, int pageSize, String sortBy, String search) {

        StringBuilder sqlQuery = new StringBuilder(
                "SELECT new com.example.demo.dto.response.UserDetailResponse(u.id, u.firstName, u.lastName, u.email, u.phone) FROM User u WHERE 1=1"
        );

//        If search mà != null or blank thì append
        if (StringUtils.hasLength(search)) {
            sqlQuery.append(" AND lower(u.firstName) LIKE lower(:firstName)");
            sqlQuery.append(" OR lower(u.lastName) LIKE lower(:lastName)");
            sqlQuery.append(" OR lower(u.email) LIKE lower(:email)");
        }

        if (StringUtils.hasLength(sortBy)) {
            // firstName:asc|desc
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                sqlQuery.append(String.format("ORDER BY u.%s %s", matcher.group(1), matcher.group(3)));
            }

        }

//        Tạo ra câu lệnh CreateQuery
        Query selectQuery = entityManager.createQuery(sqlQuery.toString());
        selectQuery.setFirstResult(pageNo);
        selectQuery.setMaxResults(pageSize);
//        Chuyền tham Parameter
        if (StringUtils.hasLength(search)) {
            selectQuery.setParameter("firstName", String.format("%%%s%%", search));
            selectQuery.setParameter("lastName", String.format("%%%s%%", search));
            selectQuery.setParameter("email", String.format("%%%s%%", search));
        }

        List user = selectQuery.getResultList();


        System.out.println(user);
//        query ra list user

//        query so record
        StringBuilder sqlCountQuery = new StringBuilder("SELECT COUNT(*) FROM User u WHERE 1=1");
        if (StringUtils.hasLength(search)) {
            sqlCountQuery.append(" AND lower(u.firstName) LIKE lower(?1)");
            sqlCountQuery.append(" OR lower(u.lastName) LIKE lower(?2)");
            sqlCountQuery.append(" OR lower(u.email) LIKE lower(?3)");
        }

        Query selectCountQuery = entityManager.createQuery(sqlCountQuery.toString());

        if (StringUtils.hasLength(search)) {
            selectCountQuery.setParameter(1, String.format("%%%s%%", search));
            selectCountQuery.setParameter(2, String.format("%%%s%%", search));
            selectCountQuery.setParameter(3, String.format("%%%s%%", search));
        }

        Long totalElements = (Long) selectCountQuery.getSingleResult();

        Page<?> page = new PageImpl<Object>(user, PageRequest.of(pageNo, pageSize), totalElements);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSiz(pageSize)
                .totalElement(page.getTotalPages())
                .items(page.stream().toList()).build();
    }

//    firstName, Address
    public PageResponse<?> advanceSearchByCriteria(int pageNo,int pageSize,String sortBy,List<String> search, String address) {

        //        firstName:T, lastName:T
        List<SearchCritetia> searchCritetias = new ArrayList<>();
        //        1. lấy ra danh sách user
        if(search != null){
            for (String sortBys : search) {
                if (StringUtils.hasLength(sortBys)) {
                    // firstName:value
                    Pattern pattern = Pattern.compile("(\\w+?)(:|>|<)(.*)");
                    Matcher matcher = pattern.matcher(sortBys);
                    if (matcher.find()) {
                        // todo
                        searchCritetias.add(new SearchCritetia(matcher.group(1), matcher.group(2), matcher.group(3) ));
                    }
                }
            }
        }

        //        2. số lượng bản ghi và phân trang

        List<User> users = getUsers(pageNo, pageSize, searchCritetias, sortBy, address);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSiz(pageSize)
                .totalElement(0)
                .items(users)
                .build();
    }

    private List<User> getUsers(int pageNo, int pageSize, List<SearchCritetia> searchCritetias, String sortBy, String address) {
        // 1. Khỏi tạo Builder
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // 2. Khởi tạo query
        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);

        // 3. Định nghiax đối tượng truy suất tìm kiếm
        Root<User> root = query.from(User.class);

        // Xử lý các điều kiện
        Predicate predicate = criteriaBuilder.conjunction();
        UserSearchQueryCriteriaConsumer queryCriteriaConsumer = new UserSearchQueryCriteriaConsumer(criteriaBuilder, root , predicate);

        // search address
        if(StringUtils.hasLength(address)){
            Join<Address, User> addressUserJoin = root.join("addresses");
            Predicate addressPredicate = criteriaBuilder.like(addressUserJoin.get("city"), "%" + address +"%" );
            query.where(predicate, addressPredicate);
        }else{
            searchCritetias.forEach(queryCriteriaConsumer);
            predicate = queryCriteriaConsumer.getPredicate();

            query.where(predicate);
        }

        // Sort
        if (StringUtils.hasLength(sortBy)) {
            // firstName:asc|desc
            Pattern pattern = Pattern.compile("(\\w+?)(:)(asc|desc)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                if(matcher.group(3).equalsIgnoreCase("desc")){
                    query.orderBy(criteriaBuilder.desc(root.get(columnName)));
                }else{
                    query.orderBy(criteriaBuilder.asc(root.get(columnName)));
                }
            }

        }

        return entityManager.createQuery(query).setFirstResult(pageNo).setMaxResults(pageSize).getResultList();

    }
}