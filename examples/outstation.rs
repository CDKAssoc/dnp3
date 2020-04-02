use dnp3rs::app::parse::parser::Request;
use dnp3rs::transport::reader::Reader;
use std::net::SocketAddr;
use std::str::FromStr;
use tokio::net::TcpListener;

#[tokio::main(threaded_scheduler)]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    simple_logger::init_with_level(log::Level::Info).unwrap();

    let mut listener = TcpListener::bind(SocketAddr::from_str("127.0.0.1:20000")?).await?;

    let (mut socket, _) = listener.accept().await?;

    let mut reader = Reader::new(false, 1024);

    loop {
        let asdu = reader.read(&mut socket).await.unwrap();

        match Request::parse(asdu.data) {
            Err(err) => {
                log::warn!("bad request: {:?}", err);
            }
            Ok(request) => {
                if let Err(err) = request.parse_objects() {
                    log::warn!("bad request object: {:?}", err);
                }
            }
        }
    }
}