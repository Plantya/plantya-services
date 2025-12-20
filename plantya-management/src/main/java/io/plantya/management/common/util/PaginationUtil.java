package io.plantya.management.common.util;

import io.plantya.management.enums.UserBadRequestMessage;
import io.plantya.management.exception.BadRequestException;

public class PaginationUtil {

    public PaginationUtil() {}

    public static boolean validateAndUsePaging(Integer page, Integer size) {
        boolean usePaging = page != null || size != null;

        if (usePaging && (page == null || size == null)) {
            throw new BadRequestException(UserBadRequestMessage.USER_PAGINATION_PARAMETER_INCOMPLETE);
        }

        if (usePaging && (page < 1 || size < 1)) {
            throw new BadRequestException(UserBadRequestMessage.USER_PAGINATION_PARAMETER_INVALID);
        }

        return usePaging;
    }

    public static void validateOrder(String order) {
        if (order == null || order.isBlank()) {
            return;
        }

        String upper = order.toUpperCase();
        if (!upper.equals("ASC") && !upper.equals("DESC")) {
            throw new BadRequestException(UserBadRequestMessage.USER_ORDER_INVALID);
        }
    }

}
