
import { Component, OnInit } from '@angular/core';

import { HttpClient } from '@angular/common/http';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

import { Router, ActivatedRoute, ParamMap } from '@angular/router';

import { map, Observable } from 'rxjs';

import { Vacation } from '../../model/vacation';
import {PurchaseDataService} from "../../services/purchase-data.service";
import {VacationDto} from "../../model/dto/vacation-dto";
import {CartItemDto} from "../../model/dto/cart-item-dto";
import {PurchaseDto} from "../../model/dto/purchase-dto";
import {CustomerDto} from "../../model/dto/customer-dto";
import {CartDto} from "../../model/dto/cart-dto.model";
import {environment} from "../../../environments/environment";

@Component({
  selector: 'app-vacation-detail',
  templateUrl: './vacation-detail.component.html',
  styleUrls: ['./vacation-detail.component.css']
})
export class VacationDetailComponent implements OnInit {

  vacationUrl = environment.URL + '/api/vacations' || '/api/vacations';

  vacation: Vacation = new Vacation("", "", 0, "", new Date(), new Date(), { self: { href: "" }});
  vacationId: number = 0;

  purchaseServiceDto: any;

  constructor(private http: HttpClient, private route: ActivatedRoute,
              private sanitizer: DomSanitizer,
              private purchaseDataService: PurchaseDataService) { }

  ngOnInit(): void {

    // get data service
    this.purchaseDataService.purchaseServiceData.subscribe((serviceData) =>{
      this.purchaseServiceDto = serviceData;
    })

    this.vacationId = +this.route.snapshot.paramMap.get('vacationId')!;
    this.getVacation(this.vacationId).subscribe(vacation => this.vacation = vacation);

  }

  getVacation(vacationId: number): Observable<Vacation> {
    return this.http.get<Vacation>(`${this.vacationUrl}/${vacationId}`)
        .pipe(
          map(vacation => vacation)
        )
  }

  ngOnDestroy(){

      this.purchaseServiceDto.getCurrentCartItem().getVacation().setId(this.vacation.id);
      this.purchaseServiceDto.getCurrentCartItem().getVacation().setVacationTitle(this.vacation.vacation_title);
      this.purchaseServiceDto.getCurrentCartItem().getVacation().setDescription(this.vacation.description);
      this.purchaseServiceDto.getCurrentCartItem().getVacation().setTravelPrice(this.vacation.travel_price);
      this.purchaseServiceDto.getCurrentCartItem().getVacation().setImageUrl(this.vacation.image_URL);
  }

  ngAfterViewInit(){

      // create and set vacation id for later use
      let tempVacationDto = new VacationDto(0, "", "", 0, "");
      tempVacationDto.setId(this.vacationId);
      tempVacationDto.setVacationTitle(this.vacation.vacation_title);
      tempVacationDto.setTravelPrice(this.vacation.travel_price);

      // new cart item
      let tempCartItemDto = new CartItemDto(tempVacationDto, []);
      // set the vacation in the cart item
      tempCartItemDto.setVacation(tempVacationDto);

      this.purchaseServiceDto.addCartItem(tempCartItemDto);

    }

    onImgError(event: Event) {
      const img = event?.target as HTMLImageElement | null;
      if (img) {
        console.error('Image failed to load:', img.src);
        img.onerror = null as any;
        img.src = 'https://placehold.co/800x500?text=No+Image';
      }
    }

    getSafeImageUrl(url: string | undefined): SafeUrl | undefined {
      if (!url) return undefined;
      console.log('Processing image URL:', url);
      
      // Some URLs might need https protocol or other adjustments
      if (url.startsWith('http://')) {
        url = url.replace('http://', 'https://');
      }
      
      return this.sanitizer.bypassSecurityTrustUrl(url);
    }

}
