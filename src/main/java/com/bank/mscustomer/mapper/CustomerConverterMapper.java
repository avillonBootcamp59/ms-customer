package com.bank.mscustomer.mapper;

import com.bank.mscustomer.dto.CustomerSummaryDTO;
import com.bank.mscustomer.entity.CustomerEntity;
import com.bank.mscustomer.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerConverterMapper {

  @Mappings({})
  ListCustomerDataResponse toListCustomerDataResponse(CustomerEntity entity);
  CreateCustomerDataResponse toCreateCustomerDataResponse(CustomerEntity entity);
  List<ListCustomerDataResponse> toListCustomerDataResponseList(List<CustomerEntity> entities);
  CustomerEntity toCustomerEntity(CreateCustomerRequest request);
  UpdateCustomerDataResponse toUpdateCustomerDataResponse(CustomerEntity updatedCustomer);
  CustomerSummary toCustomerSummary(CustomerSummaryDTO summaryDTO);
}
