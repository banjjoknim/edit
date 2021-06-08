package com.app.edit.utils;

import com.app.edit.enums.UserRole;
import org.springframework.stereotype.Controller;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

@Convert
public class UserRoleConverter implements AttributeConverter<UserRole,String> {

    /**
     * (Enum -> db데이터)
     * @param attribute
     * @return
     */
    @Override
    public String convertToDatabaseColumn(UserRole attribute) {
        return attribute.name();
    }

    /**
     * (db데이터 -> Enum)
     * @param dbData
     * @return
     */
    @Override
    public UserRole convertToEntityAttribute(String dbData) {
        return UserRole.dataToEnum(dbData);
    }
}
