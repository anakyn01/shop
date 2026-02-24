import Carousel from 'react-bootstrap/Carousel';

export default function SmallBanner(){

    return(
<>
<Carousel fade>
  <Carousel.Item>

    <Carousel.Caption>
              <img
          className="d-block w-100"
          src="/img/slide.png"
          alt="First slide"
    width="100%"   // 여기서 width를 지정해 주세요.
      height="100px"  // height을 "auto"로 지정해서 비율을 유지합니다.
        />
      <h3>블라블라</h3>
      <p>Lorem ipsum dolor sit amet consectetur adipisicing elit. Sunt reiciendis, consectetur iste ex enim harum, iusto quas quasi magnam possimus est! Maxime quod at veniam fugiat a error nisi iure!</p>
    </Carousel.Caption>
  </Carousel.Item>

  <Carousel.Item>
              <img
          className="d-block w-100"
          src="/img/slide2.png"
          alt="First slide"
                width="100%"   // 여기서 width를 지정해 주세요.
      height="100px"  // height을 "auto"로 지정해서 비율을 유지합니다.
        />
    <Carousel.Caption>

      <h3>블라블라</h3>
      <p>Lorem ipsum dolor sit amet consectetur adipisicing elit. Sunt reiciendis, consectetur iste ex enim harum, iusto quas quasi magnam possimus est! Maxime quod at veniam fugiat a error nisi iure!</p>
    </Carousel.Caption>
  </Carousel.Item>

  <Carousel.Item>
          <img
          className="d-block w-100"
          src="/img/slide3.png"
          alt="First slide"
          height="100px"
        />
    <Carousel.Caption>
      <h3>블라블라</h3>
      <p>Lorem ipsum dolor sit amet consectetur adipisicing elit. Sunt reiciendis, consectetur iste ex enim harum, iusto quas quasi magnam possimus est! Maxime quod at veniam fugiat a error nisi iure!</p>
    </Carousel.Caption>
  </Carousel.Item>


</Carousel>

</>
    );

}
/*
 */