package io.plantya.user.service;

import io.plantya.user.common.dto.param.UserParam;
import io.plantya.user.common.dto.request.UserQueryParam;
import io.plantya.user.common.exception.BadRequestException;
import io.plantya.user.common.exception.NotFoundException;
import io.plantya.user.common.mapper.ResponseMapper;
import io.plantya.user.domain.User;
import io.plantya.user.dto.response.PagedUserResponse;
import io.plantya.user.dto.response.UserDeletedResponse;
import io.plantya.user.dto.response.UserGetResponse;
import io.plantya.user.repository.UserDeletedRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;

import static io.plantya.user.common.exception.message.ErrorMessage.PAGE_LOWER_THAN_ONE;
import static io.plantya.user.common.exception.message.ErrorMessage.USER_NOT_FOUND;

@ApplicationScoped
public class UserDeletedService {

    @Inject
    UserDeletedRepository userDeletedRepository;

    private final Logger LOG = Logger.getLogger(UserDeletedService.class);

    public PagedUserResponse<UserDeletedResponse> findAllDeletedUsers(UserQueryParam param) {
        if (param.getPage() < 1) {
            throw new BadRequestException(PAGE_LOWER_THAN_ONE);
        }

        UserParam userParam = new UserParam(
                param.getPage(),
                param.getSize(),
                param.getSearch(),
                param.getSort(),
                param.getOrder(),
                param.getRole()
        );

        List<User> devices = userDeletedRepository.findAllDeletedUsers(userParam);
        long totalData = userDeletedRepository.countDeletedUsers(userParam);

        List<UserDeletedResponse> responses = devices.stream()
                .map(ResponseMapper::toUserDeletedResponse)
                .toList();

        int totalPages = (int) Math.ceil((double) totalData / param.getSize());

        return new PagedUserResponse<>(
                responses.size(),
                param.getPage(),
                param.getSize(),
                totalPages,
                responses
        );
    }

    public UserDeletedResponse findDeletedByUserId(String userId) {
        User user = userDeletedRepository.findDeletedByUserId(userId);
        if (user == null) {
            throw new NotFoundException(USER_NOT_FOUND);
        }

        return ResponseMapper.toUserDeletedResponse(user);
    }

    @Transactional
    public UserGetResponse restoreUser(String userId) {
        LOG.infof("Restore user: userId=%s", userId);

        User user = userDeletedRepository.findDeletedByUserId(userId);
        if (user == null || user.getDeletedAt() == null) {
            LOG.warnf("Restore user failed - user not found or not deleted: userId=%s", userId);
            throw new NotFoundException(USER_NOT_FOUND);
        }

        userDeletedRepository.restoreUser(user);

        LOG.infof("User restored successfully: userId=%s", userId);
        return ResponseMapper.toUserGetResponse(user);
    }
}
