package com.example.demo.service.impl;

import com.example.demo.configuration.AppConfig;
import com.example.demo.dto.request.AddressDTO;
import com.example.demo.dto.request.UserRequestDTO;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.UserDetailResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.infrastructure.util.UserStatus;
import com.example.demo.infrastructure.util.UserType;
import com.example.demo.model.Address;
import com.example.demo.model.User;
import com.example.demo.repository.SearchRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SearchRepository searchRepository;

    @Autowired
    private ModelMapper mapper;


    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public long saveUser(UserRequestDTO userRequestDTO) {
        User user = User.builder()
                .firstName(userRequestDTO.getFirstName())
                .lastName(userRequestDTO.getLastName())
                .dateOfBirth(userRequestDTO.getDateOfBirth())
                .gender(userRequestDTO.getGender())
                .phone(userRequestDTO.getPhone())
                .email(userRequestDTO.getEmail())
                .username(userRequestDTO.getUsername())
                .password(passwordEncoder.encode(userRequestDTO.getPassword()))
                .status(userRequestDTO.getStatus())
                .type(UserType.valueOf(userRequestDTO.getType().toUpperCase())) // new HashSet<>()
                .build();
        userRequestDTO.getAddresses().forEach(a ->
                user.saveAdd(Address.builder()
                        .apartmentNumber(a.getApartmentNumber())
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build())
        );


        userRepository.save(user);

        log.info("User has saved");
        return user.getId();
    }

    @Override
    public void updateUser(long userId, UserRequestDTO userRequestDTO) {
        User user = getUserById(userId);
        user.setFirstName(userRequestDTO.getFirstName());
//        Cả trống cả null StringUtils.hasLength
        if (StringUtils.hasLength(userRequestDTO.getUsername())) {
            user.setUsername(userRequestDTO.getUsername());
        }
        user.setLastName(userRequestDTO.getLastName());
        user.setDateOfBirth(userRequestDTO.getDateOfBirth());
        user.setGender(userRequestDTO.getGender());
        user.setPhone(userRequestDTO.getPhone());
//      Check email
        if (!userRequestDTO.getEmail().equals(user.getEmail())) {
//            check email from database if not exist then allow update email otherwise throw ex
            user.setEmail(userRequestDTO.getEmail());
        }
        user.setUsername(userRequestDTO.getUsername());
        user.setPassword(userRequestDTO.getPassword());
        user.setStatus(userRequestDTO.getStatus());
        user.setType(UserType.valueOf(userRequestDTO.getType().toUpperCase()));
        user.setAddresses(convert(userRequestDTO.getAddresses()));
        userRepository.save(user);

        log.info("User updated successfully");

    }

    @Override
    public void changeStatus(long userId, UserStatus status) {
        User user = getUserById(userId);
        user.setStatus(status);

        userRepository.save(user);

        log.info("Change status successfully");

    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);

        log.info("Delete successfully USERID= {}", userId);
    }


    @Override
    public UserDetailResponse getUser(long userId) {
        User user = getUserById(userId);
        return UserDetailResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();
    }

    @Override
    public PageResponse<?> getAllUserWithSortBy(int pageNo, int pageSize, String sortBy) {
//        pageNumber = 1, pageSize = 10 / 5
//        [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14]
        int p = 0;
        if (pageNo > 0) {
            p = pageNo - 1;
        }

        List<Sort.Order> sorts = new ArrayList<>();

        if (StringUtils.hasLength(sortBy)) {
            // firstName:asc|desc
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    sorts.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                } else {
                    sorts.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                }
            }
        }

        Pageable pageable = PageRequest.of(p, pageSize, Sort.by(sorts));

        Page<User> users = userRepository.findAll(pageable);

        List<UserDetailResponse> userResponse = users.stream().map(
                user -> UserDetailResponse.builder()
                        .id(user.getId())
                        .lastName(user.getLastName())
                        .firstName(user.getFirstName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .build()
        ).collect(Collectors.toList());

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSiz(pageSize)
                .totalElement(Math.toIntExact(users.getTotalElements()))
                .items(userResponse)
                .build();
    }

    @Override
    public PageResponse<?> getAllUserWithSortByMultipleColumns(int pageNo, int pageSize, List<String> sortBy) {
        int p =0;
        if (pageNo > 0) {
            p = pageNo - 1;
        }

        List<Sort.Order> orders = new ArrayList<>();

        for (String sortBys : sortBy) {
            if (StringUtils.hasLength(sortBys)) {
                // firstName:asc|desc
                Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
                Matcher matcher = pattern.matcher(sortBys);
                if (matcher.find()) {
                    if (matcher.group(3).equalsIgnoreCase("asc")) {
                        orders.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                    } else {
                        orders.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                    }
                }
            }
        }


        Pageable pageable = PageRequest.of(p, pageSize, Sort.by(orders));

        Page<User> users = userRepository.findAll(pageable);

        List<UserDetailResponse> userDetailResponses = users.stream().map(user -> UserDetailResponse.builder()
                .id(user.getId())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build()
        ).collect(Collectors.toList());

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSiz(pageSize)
                .totalElement(Math.toIntExact(users.getTotalElements()))
                .items(userDetailResponses)
                .build();

    }

    @Override
    public PageResponse<?> getAllUserWithSortByMultipleColumnsAndSearch(int pageNo, int pageSize, String sortBy, String search) {
        return searchRepository.getAllUserWithSortByMultipleColumnsAndSearch(pageNo, pageSize, sortBy, search);
    }

    @Override
    public PageResponse<?> advanceSearchByCriteria(int pageNo, int pageSize, String sortBy, List<String> search, String address) {
        return searchRepository.advanceSearchByCriteria(pageNo, pageSize, sortBy,search, address);
    }

    public Set<Address> convert(Set<AddressDTO> addressDTO) {
        Set<Address> addressSet = new HashSet<>();
        addressDTO.forEach(a ->
                addressSet.add(Address.builder()
                        .apartmentNumber(a.getApartmentNumber())
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build())
        );
        return addressSet;
    }

    //    Ném ra Exception bắt nó trong logic
    private User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User not found")
                );
    }
}
