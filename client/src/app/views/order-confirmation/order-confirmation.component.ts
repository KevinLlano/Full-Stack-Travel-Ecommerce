import {HttpClient} from '@angular/common/http';
import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {CartItem} from 'src/app/model/cart-item';
import {Customer} from 'src/app/model/customer';
import {PurchaseApiResponse} from 'src/app/model/purchase-api-response';
import {Vacation} from 'src/app/model/vacation';
import {PurchaseDataService} from "../../services/purchase-data.service";
import {CustomerDto} from "../../model/dto/customer-dto";
import {CartDto} from "../../model/dto/cart-dto.model";
import {PurchaseDto} from "../../model/dto/purchase-dto";
import {StatusType} from "../../model/StatusType";
import {VacationDto} from "../../model/dto/vacation-dto";
import {ExcursionDto} from "../../model/dto/excursion-dto";
import {CartItemDto} from "../../model/dto/cart-item-dto";
import {environment} from "../../../environments/environment";


@Component({
  selector: 'app-order-confirmation',
  templateUrl: './order-confirmation.component.html',
  styleUrls: ['./order-confirmation.component.css']
})
export class OrderConfirmationComponent implements OnInit {

  cartItemsUrl = "";
  checkoutUrl =  environment.URL + '/api/checkout/purchase' || "http://localhost:8080/api/checkout/purchase";
  customerUrl = environment.URL + '/api/customers/1' || "http://localhost:8080/api/customers/1";
  cartsUrl = environment.URL + '/api/carts' || "http://localhost:8080/api/carts";
  cartId = 0;

  cartItems: CartItem[] = [];
  vacations: Set<Vacation> = new Set();
  customer: Customer = new Customer(0, "", "", "", "", "", 0)

  orderTrackingNumber: string = ""

  purchaseServiceDto: any;

  customerDto: CustomerDto = new CustomerDto(0, "", "", "", "", "");

  constructor(private http: HttpClient,
              private route: ActivatedRoute,
              private purchaseDataService: PurchaseDataService
  ) {
  }

  ngOnInit(): void {
    // new purchase data service dto
    this.purchaseDataService.purchaseServiceData.subscribe((serviceData) => {
      // console.log('Excursion Detail Current Data Service:', serviceData);
      this.purchaseServiceDto = serviceData;
    })

    // get customer dto from data service
    this.customerDto = this.purchaseServiceDto.getCustomer();

    this.checkout();

  }

  ngOnDestroy() {
    // reset the data service
    let status: StatusType = StatusType.pending;
    let tempCustomer: CustomerDto = new CustomerDto(0, "", "", "", "", "");
    let tempVacation: VacationDto = new VacationDto(0, "", "", 0, "");
    let tempCart: CartDto = new CartDto(0, 0, 1, status, tempCustomer);
    let tempExcursionList: ExcursionDto[] = [];
    let tempCartItem: CartItemDto = new CartItemDto(tempVacation, tempExcursionList);
    let tempCartItemList: CartItemDto[] = [];
    let purchaseDataDto: PurchaseDto = new PurchaseDto(tempCustomer, tempCart, tempCartItemList);

    this.purchaseDataService.setData(purchaseDataDto);
  }

  checkout() {
    if (!this.purchaseServiceDto) {
      console.error('No purchaseServiceDto available');
      return;
    }

    // Build payload explicitly because class private fields won't serialize with Object.assign
    const customer = this.purchaseServiceDto.getCustomer();
    const cart = this.purchaseServiceDto.getCart();
    const cartItems = this.purchaseServiceDto.getCartItems();

    const payload = {
      customer: {
        id: customer.getId(),
        firstName: customer.getFirstName(),
        lastName: customer.getLastName(),
        address: customer.getAddress(),
        postal_code: customer.getPostalCode(),
        phone: customer.getPhone()
      },
      cart: {
        id: cart.getId(),
        package_price: cart.getPackagePrice(),
        party_size: cart.getPartySize(),
        status: cart.getStatus(),
        customer: { id: customer.getId() }
      },
      cartItems: cartItems.map((ci: any) => {
        const vacation = ci.getVacation();
        return {
          vacation: {
            id: vacation.getId(),
            vacation_title: vacation.getVacationTitle(),
            description: vacation.getDescription(),
            travel_price: vacation.getTravelPrice(),
            image_URL: vacation.getImageUrl()
          },
          excursions: ci.getExcursions().map((ex: any) => ({
            id: ex.getId(),
            excursion_title: ex.getExcursionTitle(),
            excursion_price: ex.getExcursionPrice(),
            image_URL: ex.getImageUrl()
          }))
        }
      })
    };

    console.log('Checkout payload =>', payload);

    this.http.post<PurchaseApiResponse>(this.checkoutUrl, payload).subscribe({
      next: (response) => {
        this.orderTrackingNumber = response.orderTrackingNumber;
        console.log('Order tracking number:', this.orderTrackingNumber);
      },
      error: (err) => {
        console.error('Checkout failed 400 payload sent:', payload);
        console.error('Backend error:', err);
      }
    });
  }
}
