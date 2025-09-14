
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';

import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Customer } from 'src/app/model/customer';
import { CustomerApiResponse } from 'src/app/model/customer-api-response';
import {environment} from "../../../environments/environment";

@Component({
  selector: 'app-view-customer',
  templateUrl: './view-customer.component.html',
  styleUrls: ['./view-customer.component.css']
})
export class ViewCustomerComponent implements OnInit {

  customerUrl = environment.URL + '/api/customers' || '/api/customers';

  customers: Customer[] = [];

  constructor(private http: HttpClient,
    private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {
    this.getCustomers().subscribe(customers => this.customers = customers);
  }

  ngAfterViewChecked(): void {
    this.cdr.detectChanges()
  }

  getCustomers(): Observable<Customer[]> {
    return this.http.get<CustomerApiResponse>(this.customerUrl)
        .pipe(
          map(response => response._embedded.customers)
        )
  }

  deleteCustomer(id: number): void {
    if (confirm('Are you sure you want to delete this customer?')) {
      this.http.delete(`${this.customerUrl}/${id}`).subscribe({
        next: () => {
          // Refresh the list after deletion
          this.getCustomers().subscribe(customers => this.customers = customers);
        },
        error: (err) => console.error('Delete failed', err)
      });
    }
  }

}
