package com.synergy.binarfood.setup;

import com.synergy.binarfood.entity.*;
import com.synergy.binarfood.repository.*;
import com.synergy.binarfood.util.random.Randomizer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class Setup {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final Random random;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void setup() {
        orderDetailRepository.hardDeleteAll();
        orderRepository.hardDeleteAll();
        productRepository.hardDeleteAll();
        merchantRepository.hardDeleteAll();
        userRepository.hardDeleteAll();
        roleRepository.hardDeleteAll();

        int numCustomers = 3;
        int numMerchants = 3;
        int minProductPerMerchant = 2;
        int maxProductPerMerchant = 5;
        int minProductPrice = 5000;
        int maxProductPrice = 100000;


        /*

            Seed roles

        */
        Set<Role> merchantRoleSet = new HashSet<>();
        Role newMerchantRole = Role.builder()
                .name(ERole.MERCHANT)
                .build();

        Set<Role> customerRoleSet = new HashSet<>();
        Role newCustomerRole = Role.builder()
                .name(ERole.CUSTOMER)
                .build();

        roleRepository.saveAllAndFlush(List.of(newMerchantRole, newCustomerRole));
        merchantRoleSet.add(newMerchantRole);
        customerRoleSet.add(newCustomerRole);


        /*

            Seed users

        */
        List<User> usersMerchants = new ArrayList<>();
        List<User> usersCustomers = new ArrayList<>();

        for (int i = 1; i <= numMerchants; i++) {
            User newUser = User.builder()
                    .name(String.format("Merchant %d", i))
                    .email(String.format("merchant%d@example.com", i))
                    .password(passwordEncoder.encode("password"))
                    .roles(merchantRoleSet)
                    .build();
            usersMerchants.add(newUser);
        }

        for (int i = 1; i <= numCustomers; i++) {
            User newUser = User.builder()
                    .name(String.format("Customer %d", i))
                    .email(String.format("customer%d@example.com", i))
                    .password(passwordEncoder.encode("password"))
                    .roles(customerRoleSet)
                    .build();
            usersCustomers.add(newUser);
        }

        userRepository.saveAllAndFlush(usersMerchants);
        userRepository.saveAllAndFlush(usersCustomers);

        log.info("[SUCCESS] Seeds {} records to users table", usersMerchants.size() + usersCustomers.size());


        /*

            Seed merchants

        */
        List<Merchant> merchants = new ArrayList<>();

        for (User uM : usersMerchants) {
            Merchant newMerchant = Merchant.builder()
                    .name("Toko " + uM.getName())
                    .location(RandomStringUtils.randomAlphabetic(10))
                    .user(uM)
                    .open(random.nextInt() % 2 == 0).build();
            merchantRepository.saveAndFlush(newMerchant);
            merchants.add(newMerchant);
        }

        merchantRepository.saveAllAndFlush(merchants);

        log.info("[SUCCESS] Seeds {} records to merchants table", merchants.size());


        /*

            Seed products

        */
        List<Product> products = new ArrayList<>();

        for (Merchant merchant : merchants) {
            int numProdPerMerchant = Randomizer.generate(minProductPerMerchant, maxProductPerMerchant);
            for (int i = 0; i < numProdPerMerchant; i++) {
                Product newProduct = Product.builder()
                        .name("Product " + RandomStringUtils.randomAlphanumeric(10))
                        .price(Randomizer.generatePrice(minProductPrice, maxProductPrice))
                        .merchant(merchant).build();
                products.add(newProduct);
            }
        }

        productRepository.saveAllAndFlush(products);

        log.info("[SUCCESS] Seeds {} records to products table", products.size());


        /*

            Seed products

        */
        List<Order> orders = new ArrayList<>();
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (User user: usersCustomers) {
            int randomOrderCount = Randomizer.generate(1, 10);
            for (int c = 0; c < randomOrderCount; c++) {
                Order newOrder = Order.builder()
                        .orderAt(new Date())
                        .destinationAddress(RandomStringUtils.randomAlphabetic(10))
                        .user(user)
                        .completedAt(new Date()).build();
                orders.add(newOrder);

                Merchant merchant = merchants.get(Randomizer.generate(0, numMerchants - 1));
                List<Product> productsOfMerchant = productRepository
                        .findAllByMerchant(merchant, Pageable.unpaged())
                        .stream()
                        .toList();

                int randomNumOrderDetail = Randomizer.generate(1, productsOfMerchant.size());
                for (int i = 0; i < randomNumOrderDetail; i++) {
                    int randQuantity = Randomizer.generate(1, 10);
                    OrderDetail newOrderDetail = OrderDetail.builder()
                            .order(newOrder)
                            .product(productsOfMerchant.get(i))
                            .quantity(randQuantity)
                            .totalPrice(randQuantity * productsOfMerchant.get(i).getPrice()).build();
                    orderDetails.add(newOrderDetail);
                }
            }
        }

        orderRepository.saveAllAndFlush(orders);
        orderDetailRepository.saveAllAndFlush(orderDetails);

        log.info("[SUCCESS] Seeds {} records to orders table", orders.size());
        log.info("[SUCCESS] Seeds {} records to order_details table", orders.size());
    }
}
