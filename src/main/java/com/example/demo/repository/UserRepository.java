package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    //    Distinict + find
    //  @Query(value = "SELECT DISTINCT FROM User u WHERE u.firstName = :firstName and u.lastName = :lastName")
    List<User> findDistinictByFirstNameAndLastName(String firstName, String lastName);

    //  @Query(value = "SELECT * FROM User u WHERE u.email = ?1")
    Optional<User> findByEmail(String email);

     Optional<User> findByUsername(String username);


    //  @Query(value = "SELECT * FROM User u WHERE u.firstName =:name OR u.lastName =:name")
//    List<User> findByFirstNameOrLastName(String name);

    // -- Is, Equals --
    //  @Query(value = "SELECT * FROM User u WHERE u.firstName =:name")
    List<User> findByFirstNameIs(String name);

    List<User> findByFirstNameEquals(String name);

    // -- Between --
    // @Query(value = "SELECT * FROM User u WHERE u.createAt BETWEEN ?1 AND ?2")
//    List<User> findBycreateAtBetween(Date startDate, Date endDate);

    // -- LessThan --

    // -- Before and After --

    // -- Is Null, Is Not Null

    // -- Like, Not Like --
    //  @Query(value = "SELECT * FROM User u WHERE u.firstName LIKE %:firstName%")
    List<User> findByLastNameLike(String firstName);

    // -- EndingWith --
    // @Query(value = "SELECT * FROM User u WHERE u.lastName not like %:lastName")
    List<User> findByLastNameEndingWith(String lastName);

    // In
//    @Query(value = "SELECT * FROM User u WHERE u.age in (:ages)")
//    List<User> findByAgeIn(Collection<Integer> ages);

    // -- IgnoreCase --
//    @Query(value = "SELECT * FROM User u WHERE LOWER(u.firstName) <> LOWER(:name)")
//    List<User> findByFirstNameIgnoreCase(String name);
}
