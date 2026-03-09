/// ${PROJECT_NAME} - A simple Rust console application
/// 
/// Author: ${AUTHOR_NAME} <${AUTHOR_EMAIL}>
/// Version: ${PROJECT_VERSION}

fn main() {
    println!("Welcome to ${PROJECT_NAME} v${PROJECT_VERSION}!");
    println!("Author: ${AUTHOR_NAME}");
    
    // Your code starts here
    let message = greet("World");
    println!("{}", message);
}

/// Returns a greeting message for the given name
fn greet(name: &str) -> String {
    format!("Hello, {}!", name)
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_greet() {
        assert_eq!(greet("Rust"), "Hello, Rust!");
    }
}
