package org.example.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.example.entities.RoleEntity;
import org.example.entities.UserEntity;
import org.example.exception.InternalServerError;
import org.example.exception.InvalidUsernameException;
import org.example.exception.UserDoesntExistException;
import org.example.exception.UserExistsException;
import org.example.model.User;
import org.example.model.UserAuth;
import org.example.model.UserRoles;
import org.example.repository.RoleRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Log4j2
public class UserService {

  private final MapperFactory mapperFactoryToEntity;
  private final MapperFactory mapperFactoryToDTO;
  private final UserRepository repository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserService(
      final UserRepository repository,
      final RoleRepository roleRepository,
      final PasswordEncoder passwordEncoder) {
    this.repository = repository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = new BCryptPasswordEncoder();

    mapperFactoryToEntity = new DefaultMapperFactory.Builder().build();
    mapperFactoryToEntity.classMap(UserAuth.class, UserEntity.class).byDefault().register();

    mapperFactoryToDTO = new DefaultMapperFactory.Builder().build();
    mapperFactoryToDTO.classMap(UserEntity.class, User.class).byDefault().register();
  }

  @Transactional
  @PreAuthorize("hasAuthority('ADMIN')")
  public User createUser(final UserAuth user) {

    if (repository.existsByUsername(user.getUsername())) {
      throw new UserExistsException(
          String.format("Username[%s] is already registered", user.getUsername()));
    }

    MapperFacade mapper = mapperFactoryToEntity.getMapperFacade();
    UserEntity entity = mapper.map(user, UserEntity.class);

    UserEntity result = null;

    try {
      entity.setPassword(passwordEncoder.encode(entity.getPassword()));
      result = repository.save(entity);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalServerError(e);
    }

    MapperFacade mapperDTO = mapperFactoryToDTO.getMapperFacade();

    return mapperDTO.map(result, User.class);
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasAuthority('ADMIN')")
  public List<User> findAll(Integer page, Integer size) {
    final Integer cPage = (page == null ? 0 : page);
    final Integer cSize = (size == null ? 20 : size);

    Page<UserEntity> entities = repository.findAll(PageRequest.of(cPage, cSize));

    if (entities.isEmpty()) {
      return new ArrayList<>();
    }

    MapperFacade mapperDTO = mapperFactoryToDTO.getMapperFacade();

    return entities.get().map(e -> mapperDTO.map(e, User.class)).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasAuthority('ADMIN') || (authentication.principal.username == #username)")
  public User getUser(final String username) {
    if (username == null) {
      throw new InvalidUsernameException(String.format("Username[%s] is invalid", username));
    }

    UserEntity user = repository.findByUsername(username);

    if (user == null) {
      throw new UserDoesntExistException(
          String.format("Username[%s] does not exist", user.getUsername()));
    }

    return mapperFactoryToDTO.getMapperFacade().map(user, User.class);
  }

  @Transactional
  @PreAuthorize("hasAuthority('ADMIN') || (authentication.principal.username == #username)")
  public User updateUser(final String username, final UserAuth user) {

    if (user.getUsername() == null || !user.getUsername().equals(username)) {
      throw new InvalidUsernameException(String.format("Username[%s] is invalid", username));
    }

    if (!repository.existsByUsername(user.getUsername())) {
      throw new UserDoesntExistException(
          String.format("Username[%s] does not exist", user.getUsername()));
    }
    UserEntity oldEntity = repository.findByUsername(user.getUsername());

    MapperFacade mapper = mapperFactoryToEntity.getMapperFacade();
    UserEntity entity = mapper.map(user, UserEntity.class);

    UserEntity result = null;

    try {
      String pwd = passwordEncoder.encode(entity.getPassword());

      if (pwd != null && !pwd.equals(oldEntity.getPassword())) {
        entity.setPassword(pwd);
      }

      entity.setId(oldEntity.getId());
      entity.setUserRoles(oldEntity.getUserRoles());
      result = repository.save(entity);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalServerError(e);
    }

    MapperFacade mapperDTO = mapperFactoryToDTO.getMapperFacade();

    return mapperDTO.map(result, User.class);
  }

  @Transactional
  @PreAuthorize("hasAuthority('ADMIN')")
  public User updateRoles(String username, List<UserRoles> roles) {

    if (!repository.existsByUsername(username)) {
      throw new UserDoesntExistException(String.format("Username[%s] does not exist", username));
    }

    Set<RoleEntity> rEntities;

    if (roles.isEmpty()) {
      rEntities = new HashSet<>();
    } else {
      rEntities =
          new HashSet<>(
              roleRepository.findAllByRoleIn(
                  roles.stream().map(ur -> ur.name()).collect(Collectors.toList())));
    }

    UserEntity oldEntity = repository.findByUsername(username);

    UserEntity result = null;

    try {
      oldEntity.setUserRoles(rEntities);
      result = repository.save(oldEntity);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalServerError(e);
    }

    MapperFacade mapperDTO = mapperFactoryToDTO.getMapperFacade();

    return mapperDTO.map(result, User.class);
  }

  @Transactional(readOnly = true)
  public List<String> getRolesForUser(final String username) {
    return roleRepository.getRolesForUser(username);
  }
}
