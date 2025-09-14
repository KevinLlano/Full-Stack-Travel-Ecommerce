import {Component, OnInit} from '@angular/core';

import {HttpClient} from '@angular/common/http';

import {Router, ActivatedRoute, ParamMap} from '@angular/router';

import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';

import {Excursion} from '../../model/excursion';
import {ExcursionApiResponse} from 'src/app/model/excursion-api-response';
import {Vacation} from 'src/app/model/vacation';
import {environment} from "../../../environments/environment";

@Component({
  selector: 'app-excursion',
  templateUrl: './excursion.component.html',
  styleUrls: ['./excursion.component.css']
})
export class ExcursionComponent implements OnInit {

  vacationUrl = environment.URL + '/api/vacations/' || '/api/vacations/';
  excursions: Excursion[] = [];
  vacationId: number = 0;
  vacationTitle: string = ''

  constructor(private http: HttpClient, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    // vacation from page
    this.vacationId = +this.route.snapshot.paramMap.get('vacationId')!;

    this.getVacationTitle().subscribe(title => {
      this.vacationTitle = title;
    });

    this.getExcursions().subscribe(excursions => {
      this.excursions = excursions.sort(function (a, b) {
        if (a.excursion_title < b.excursion_title) {
          return -1;
        }
        if (a.excursion_title > b.excursion_title) {
          return 1;
        }
        return 0;
      });
    });
  }

  getExcursions(): Observable<Excursion[]> {
    return this.http.get<ExcursionApiResponse>(this.vacationUrl + this.vacationId + '/excursions')
      .pipe(
        map(response => response._embedded.excursions)
      );
  }

  getVacationTitle(): Observable<string> {
    return this.http.get<Vacation>(this.vacationUrl + this.vacationId)
      .pipe(
        map(response => response.vacation_title)
      );
  }

  onImgError(event: Event) {
    const img = event?.target as HTMLImageElement | null;
    if (img) {
      img.onerror = null as any;
      img.src = 'https://placehold.co/600x400?text=No+Image';
    }
  }

}
